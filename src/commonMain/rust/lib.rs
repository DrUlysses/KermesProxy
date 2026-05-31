use http_body_util::{BodyExt, Full};
use hyper::{
    body::Bytes,
    server::conn::http1::Builder,
    service::service_fn,
    Request,
    Response,
    Uri
};
use hyper_util::{
    client::legacy::connect::HttpConnector,
    client::legacy::Client,
    rt::{TokioExecutor, TokioIo}
};
use p12_keystore::{KeyStore, KeyStoreEntry};
use rustls::{
    pki_types::{
        CertificateDer,
        PrivateKeyDer,
        PrivatePkcs8KeyDer,
    },
    ServerConfig
};
use std::{
    fs,
    sync::{
        Arc,
        atomic::{AtomicU64, Ordering},
    },
};
use tokio::{
    io::{AsyncReadExt, AsyncWriteExt},
    net::{TcpListener, TcpStream},
    runtime::{Builder as TokioBuilder, Handle}
};
use tokio_rustls::TlsAcceptor;

static RAW_PROXY_CONNECTION_ID: AtomicU64 = AtomicU64::new(1);

fn classify_stream_io_error(err: &std::io::Error) -> &'static str {
    if err.kind() == std::io::ErrorKind::UnexpectedEof {
        return "peer closed transport unexpectedly (usually no TLS close_notify from peer)";
    }

    let msg = err.to_string().to_lowercase();
    if msg.contains("close_notify") {
        return "TLS peer closed without close_notify";
    }

    "transport forwarding error"
}

fn decode_varint(data: &[u8], pos: &mut usize) -> u64 {
    let mut value = 0u64;
    let mut shift = 0;
    while *pos < data.len() {
        let b = data[*pos];
        *pos += 1;
        value |= ((b & 0x7f) as u64) << shift;
        if b & 0x80 == 0 {
            break;
        }
        shift += 7;
        if shift >= 64 {
            break;
        }
    }
    value
}

fn get_payload_size(header: &[u8]) -> usize {
    let mut pos = 0;
    while pos < header.len() {
        let (varint_pos, tag) = {
            let mut p = pos;
            let v = decode_varint(header, &mut p);
            (p, v)
        };
        pos = varint_pos;

        let field_number = tag >> 3;
        let wire_type = tag & 0x07;

        if field_number == 5 && wire_type == 0 {
            return decode_varint(header, &mut pos) as usize;
        }

        match wire_type {
            0 => {
                decode_varint(header, &mut pos);
            }
            1 => pos += 8,
            2 => {
                let len = decode_varint(header, &mut pos) as usize;
                pos += len;
            }
            5 => pos += 4,
            _ => break,
        }
    }
    0
}

async fn proxy_framed<R, W>(
    mut reader: R,
    mut writer: W,
    direction: &'static str,
    connection_id: u64,
) -> std::io::Result<u64>
where
    R: tokio::io::AsyncRead + Unpin,
    W: tokio::io::AsyncWrite + Unpin,
{
    let mut total_bytes = 0u64;

    loop {
        let mut header_len_buf = [0u8; 2];
        match reader.read_exact(&mut header_len_buf).await {
            Ok(_) => {}
            Err(e) if e.kind() == std::io::ErrorKind::UnexpectedEof => break,
            Err(e) => return Err(e),
        }

        let header_len = u16::from_be_bytes(header_len_buf) as usize;
        let mut header_bytes = vec![0u8; header_len];
        reader.read_exact(&mut header_bytes).await?;

        let payload_size = get_payload_size(&header_bytes);
        let mut payload_bytes = vec![0u8; payload_size];
        reader.read_exact(&mut payload_bytes).await?;

        // Write whole frame at once
        writer.write_all(&header_len_buf).await?;
        writer.write_all(&header_bytes).await?;
        writer.write_all(&payload_bytes).await?;
        writer.flush().await?;

        println!(
            "[raw-proxy:{connection_id}] {direction} forwarded frame: header_len={header_len}, payload_size={payload_size}"
        );

        total_bytes += (2 + header_len + payload_size) as u64;
    }

    Ok(total_bytes)
}

#[uniffi::export]
pub async fn start_reverse_proxy_with_pfx(
    pfx_path: &str,
    input_port: u16,
    export_port: u16,
) {
    if Handle::try_current().is_ok() {
        run_reverse_proxy_with_pfx(pfx_path, input_port, export_port).await;
        return;
    }

    let runtime = match TokioBuilder::new_current_thread()
        .enable_all()
        .build()
    {
        Ok(runtime) => runtime,
        Err(err) => {
            eprintln!("Failed to create Tokio runtime: {err}");
            return;
        }
    };

    runtime.block_on(run_reverse_proxy_with_pfx(pfx_path, input_port, export_port));
}

#[uniffi::export]
pub async fn start_raw_proxy_with_pfx(
    pfx_path: &str,
    input_port: u16,
    export_port: u16,
) {
    if Handle::try_current().is_ok() {
        run_raw_proxy_with_pfx(pfx_path, input_port, export_port).await;
        return;
    }

    let runtime = match TokioBuilder::new_current_thread()
        .enable_all()
        .build()
    {
        Ok(runtime) => runtime,
        Err(err) => {
            eprintln!("Failed to create Tokio runtime: {err}");
            return;
        }
    };

    runtime.block_on(run_raw_proxy_with_pfx(pfx_path, input_port, export_port));
}

async fn run_reverse_proxy_with_pfx(
    pfx_path: &str,
    input_port: u16,
    export_port: u16,
) {
    // ------------------------------------------------------------
    // Load PFX certificate
    // ------------------------------------------------------------
    let pfx_bytes = match fs::read(pfx_path) {
        Ok(bytes) => bytes,
        Err(err) => {
            eprintln!("Failed to read pfx file: {err}");
            return;
        }
    };

    let parsed = match KeyStore::from_pkcs12(&pfx_bytes, "") {
        Ok(parsed) => parsed,
        Err(err) => {
            eprintln!("Failed to parse pfx file: {err}");
            return;
        }
    };

    let private_key_chain = match parsed
        .entries()
        .find_map(|(_, entry)| match entry {
            KeyStoreEntry::PrivateKeyChain(private_key_chain) => Some(private_key_chain),
            _ => None,
        })
    {
        Some(chain) => chain,
        None => {
            eprintln!("missing private key chain in pfx");
            return;
        }
    };

    let cert_chain: Vec<CertificateDer<'static>> = private_key_chain
        .chain()
        .iter()
        .map(|cert| CertificateDer::from(cert.as_der().to_vec()))
        .collect();

    let key_der = PrivateKeyDer::Pkcs8(PrivatePkcs8KeyDer::from(
        private_key_chain.key().to_vec(),
    ));

    // ------------------------------------------------------------
    // Configure rustls
    // ------------------------------------------------------------
    let config = match ServerConfig::builder()
        .with_no_client_auth()
        .with_single_cert(cert_chain, key_der)
    {
        Ok(config) => config,
        Err(err) => {
            eprintln!("Failed to configure TLS cert: {err}");
            return;
        }
    };

    let acceptor = TlsAcceptor::from(Arc::new(config));

    // ------------------------------------------------------------
    // TCP listener
    // ------------------------------------------------------------
    let listener = match TcpListener::bind(("0.0.0.0", input_port)).await {
        Ok(listener) => listener,
        Err(err) => {
            eprintln!("Failed to bind listener on {input_port}: {err}");
            return;
        }
    };

    println!(
        "TLS reverse proxy listening on https://0.0.0.0:{} -> http://127.0.0.1:{}",
        input_port,
        export_port
    );

    // ------------------------------------------------------------
    // Hyper HTTP client
    // ------------------------------------------------------------
    let client: Client<HttpConnector, hyper::body::Incoming> =
        Client::builder(TokioExecutor::new())
            .build(HttpConnector::new());

    loop {
        let (stream, _) = match listener.accept().await {
            Ok(result) => result,
            Err(message) => {
                eprintln!("Failed to accept inbound connection: {message}");
                return;
            }
        };

        let acceptor = acceptor.clone();
        let client = client.clone();

        tokio::spawn(async move {
            let tls_stream = match acceptor.accept(stream).await {
                Ok(result) => result,
                Err(message) => {
                    return Err(message.to_string());
                }
            };

            let io = TokioIo::new(tls_stream);

            let service = service_fn(move |mut req: Request<hyper::body::Incoming>| {
                let client = client.clone();

                async move {
                    let path = req
                        .uri()
                        .path_and_query()
                        .map(|x| x.as_str())
                        .unwrap_or("/");

                    let uri: Uri = format!(
                        "http://127.0.0.1:{}{}",
                        export_port,
                        path
                    ).parse().unwrap();

                    *req.uri_mut() = uri;

                    match client.request(req).await {
                        Ok(resp) => Ok::<_, hyper::Error>(resp.map(|body| body.boxed())),
                        Err(err) => {
                            eprintln!("Proxy error: {:?}", err);

                            Ok(Response::builder()
                                .status(502)
                                .body(
                                    Full::new(Bytes::from_static(b"Bad Gateway"))
                                        .map_err(|never| match never {})
                                        .boxed(),
                                )
                                .unwrap())
                        }
                    }
                }
            });

            if let Err(err) = Builder::new()
                .serve_connection(io, service)
                .await
            {
                Err(err.to_string())
            } else { 
                Ok(true)
            }
        });
    }
}

async fn run_raw_proxy_with_pfx(
    pfx_path: &str,
    input_port: u16,
    export_port: u16,
) {
    let pfx_bytes = match fs::read(pfx_path) {
        Ok(bytes) => bytes,
        Err(err) => {
            eprintln!("Failed to read pfx file: {err}");
            return;
        }
    };

    let parsed = match KeyStore::from_pkcs12(&pfx_bytes, "") {
        Ok(parsed) => parsed,
        Err(err) => {
            eprintln!("Failed to parse pfx file: {err}");
            return;
        }
    };

    let private_key_chain = match parsed
        .entries()
        .find_map(|(_, entry)| match entry {
            KeyStoreEntry::PrivateKeyChain(private_key_chain) => Some(private_key_chain),
            _ => None,
        })
    {
        Some(chain) => chain,
        None => {
            eprintln!("missing private key chain in pfx");
            return;
        }
    };

    let cert_chain: Vec<CertificateDer<'static>> = private_key_chain
        .chain()
        .iter()
        .map(|cert| CertificateDer::from(cert.as_der().to_vec()))
        .collect();

    let key_der = PrivateKeyDer::Pkcs8(PrivatePkcs8KeyDer::from(
        private_key_chain.key().to_vec(),
    ));

    let config = match ServerConfig::builder()
        .with_no_client_auth()
        .with_single_cert(cert_chain, key_der)
    {
        Ok(config) => config,
        Err(err) => {
            eprintln!("Failed to configure TLS cert: {err}");
            return;
        }
    };

    let acceptor = TlsAcceptor::from(Arc::new(config));

    let listener = match TcpListener::bind(("0.0.0.0", input_port)).await {
        Ok(listener) => listener,
        Err(err) => {
            eprintln!("Failed to bind listener on {input_port}: {err}");
            return;
        }
    };

    println!(
        "TLS raw proxy listening on https://0.0.0.0:{} -> tcp://127.0.0.1:{}",
        input_port,
        export_port
    );

    loop {
        let (stream, _) = match listener.accept().await {
            Ok(result) => result,
            Err(message) => {
                eprintln!("Failed to accept inbound connection: {message}");
                return;
            }
        };

        let acceptor = acceptor.clone();
        let connection_id = RAW_PROXY_CONNECTION_ID.fetch_add(1, Ordering::Relaxed);

        tokio::spawn(async move {
            println!(
                "[raw-proxy:{connection_id}] inbound tcp accepted on {input_port}"
            );

            let tls_stream = match acceptor.accept(stream).await {
                Ok(result) => result,
                Err(message) => {
                    eprintln!("[raw-proxy:{connection_id}] TLS accept error: {message}");
                    return;
                }
            };

            println!("[raw-proxy:{connection_id}] TLS handshake completed");

            let backend = match TcpStream::connect(("127.0.0.1", export_port)).await {
                Ok(result) => result,
                Err(message) => {
                    eprintln!(
                        "[raw-proxy:{connection_id}] Failed to connect backend on {export_port}: {message}"
                    );
                    return;
                }
            };

            println!(
                "[raw-proxy:{connection_id}] backend tcp connected 127.0.0.1:{export_port}"
            );

            let (mut tls_reader, mut tls_writer) = tokio::io::split(tls_stream);
            let (mut backend_reader, mut backend_writer) = backend.into_split();

            let client_to_backend = tokio::spawn(async move {
                let result = proxy_framed(
                    &mut tls_reader,
                    &mut backend_writer,
                    "c->b",
                    connection_id,
                ).await;
                let _ = backend_writer.shutdown().await;
                result
            });

            let backend_to_client = tokio::spawn(async move {
                let result = proxy_framed(
                    &mut backend_reader,
                    &mut tls_writer,
                    "b->c",
                    connection_id,
                ).await;
                let _ = tls_writer.shutdown().await;
                result
            });

            let client_to_backend_result = match client_to_backend.await {
                Ok(result) => result,
                Err(err) => {
                    eprintln!("[raw-proxy:{connection_id}] c->b join error: {err}");
                    return;
                }
            };

            let backend_to_client_result = match backend_to_client.await {
                Ok(result) => result,
                Err(err) => {
                    eprintln!("[raw-proxy:{connection_id}] b->c join error: {err}");
                    return;
                }
            };

            match client_to_backend_result {
                Ok(bytes) => {
                    println!(
                        "[raw-proxy:{connection_id}] c->b stream closed after {bytes} bytes"
                    );
                }
                Err(err) => {
                    eprintln!(
                        "[raw-proxy:{connection_id}] c->b stream error: kind={:?}, class={}, err={}",
                        err.kind(),
                        classify_stream_io_error(&err),
                        err
                    );
                }
            }

            match backend_to_client_result {
                Ok(bytes) => {
                    println!(
                        "[raw-proxy:{connection_id}] b->c stream closed after {bytes} bytes"
                    );
                }
                Err(err) => {
                    eprintln!(
                        "[raw-proxy:{connection_id}] b->c stream error: kind={:?}, class={}, err={}",
                        err.kind(),
                        classify_stream_io_error(&err),
                        err
                    );
                }
            }

            println!("[raw-proxy:{connection_id}] forwarding session finished");
        });
    }
}

// This generates extra Rust code required by UniFFI.
uniffi::setup_scaffolding!();
