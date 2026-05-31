@file:OptIn(ExperimentalSerializationApi::class)

package kermes.proxy.server

import bgs.protocol.authentication.v1.LogonRequest
import bgs.protocol.authentication.v1.VerifyWebCredentialsRequest
import bnet.protocol.Header
import bnet.protocol.NoData
import bnet.protocol.ProcessId
import bnet.protocol.connection.v1.ConnectRequest
import bnet.protocol.connection.v1.ConnectResponse
import bnet.protocol.connection.v1.DisconnectRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kermes.proxy.server.models.OriginalHash
import kotlinx.coroutines.*
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.ExperimentalSerializationApi
import platform.posix.getpid
import kotlin.time.Clock

private val logger = KotlinLogging.logger {}

@Rpc
interface BattleNetRpc {
    suspend fun connect(request: ConnectRequest): ConnectResponse
    suspend fun keepAlive(request: NoData)
    suspend fun disconnect(request: DisconnectRequest)
    suspend fun logon(request: LogonRequest)
    suspend fun verifyWebCredentials(request: VerifyWebCredentialsRequest)
}

class BnetSessionState {
    var clientId: ProcessId? = null
    var isAuthenticated: Boolean = false
    var accountId: String? = null
}

class BattleNetRpcImpl(
    private val state: BnetSessionState
) : BattleNetRpc {
    override suspend fun connect(
        request: ConnectRequest
    ): ConnectResponse = Clock.System.now().let { now ->
        state.clientId = request.client_id

        ConnectResponse(
            client_id = request.client_id,
            server_id = ProcessId(
                label = getpid(),
                epoch = now.epochSeconds.toInt(),
            ),
            server_time = now.toEpochMilliseconds(),
            use_bindless_rpc = request.use_bindless_rpc,
        )
    }

    override suspend fun keepAlive(request: NoData) {
        logger.debug { "KeepAlive received for client ${state.clientId}" }
    }

    override suspend fun disconnect(
        request: DisconnectRequest
    ) {
        logger.debug { "Disconnect received for client ${state.clientId}, reason: ${request.error_code}" }
    }

    override suspend fun logon(
        request: LogonRequest
    ) {
        logger.info { "Logon request for ${request.program} from client ${state.clientId}" }
        // Advance state here
    }

    override suspend fun verifyWebCredentials(
        request: VerifyWebCredentialsRequest
    ) {
        logger.info { "VerifyWebCredentials for client ${state.clientId}" }
        state.isAuthenticated = true
    }
}

suspend fun startBNetTcpServer(host: String, port: Int) = coroutineScope {
    val selectorManager = SelectorManager(Dispatchers.Default)
    val serverSocket = aSocket(selectorManager).tcp().bind(host, port)

    logger.info { "BNet Framed TCP Server listening on $host:$port" }

    while (isActive) {
        val socket = serverSocket.accept()
        launch {
            handleBNetConnection(socket, selectorManager)
        }
    }
}

private suspend fun handleBNetConnection(
    socket: Socket,
    selectorManager: SelectorManager
) {
    val remoteAddress = socket.remoteAddress
    logger.debug { "New BNet connection from $remoteAddress" }

    val receiveChannel = socket.openReadChannel()
    val sendChannel = socket.openWriteChannel(autoFlush = true)

    val state = BnetSessionState()
    val session = BnetSession(
        rpc = BattleNetRpcImpl(state)
    )

    try {
        val buffer = ByteArray(4096)
        while (!receiveChannel.isClosedForRead) {
            val read = receiveChannel.readAvailable(buffer)
            if (read <= 0) {
                if (read == -1) break
                yield()
                continue
            }

            val responses = session.onData(buffer.copyOfRange(0, read))

            for (response in responses) {
                sendChannel.writeFully(response)
            }
        }
    } catch (e: Exception) {
        if (e !is kotlinx.coroutines.CancellationException) {
            logger.error(e) { "Error in BNet connection $remoteAddress" }
        }
    } finally {
        socket.close()
        logger.debug { "BNet connection $remoteAddress closed" }
    }
}

class BnetSession(
    private val rpc: BattleNetRpc
) {
    private val buffer = Buffer()

    suspend fun onData(data: ByteArray): List<ByteArray> {
        val responses = mutableListOf<ByteArray>()

        buffer.write(data)

        while (true) {
            if (buffer.size < 2) return responses

            val headerSize = peekU16(buffer) ?: return responses
            if (buffer.size < headerSize.toInt() + 2) return responses

            buffer.skip(2)

            val headerBytes = try {
                buffer.readByteArray(headerSize.toInt())
            } catch (e: Exception) {
                logger.error(e) { "Failed to read header bytes of size $headerSize" }
                return responses
            }

            val header = try {
                Header.ADAPTER.decode(headerBytes)
            } catch (e: Exception) {
                logger.error(e) { "Failed to decode Header protobuf" }
                return responses
            }

            val payloadSize = header.size?.toLong() ?: 0
            if (buffer.size < payloadSize) {
                logger.debug { "Frame payload incomplete: expected $payloadSize, have ${buffer.size}. Rewinding." }
                rewind(headerSize, headerBytes)
                return responses
            }

            val payload = buffer.readByteArray(payloadSize.toInt())

            if (header.service_id != 254 && header.service_hash != 0) {
                dispatch(header, payload)?.let { responses += it }
            } else {
                logger.debug { "Skipping special/ping service frame: id=${header.service_id}, hash=${header.service_hash}" }
            }
        }
    }

    private suspend fun dispatch(
        header: Header,
        payload: ByteArray,
    ): ByteArray? = when (header.service_hash?.toUInt() to header.method_id?.toUInt()) {
        OriginalHash.ConnectionService.value to 1u -> {
            val response = rpc.connect(ConnectRequest.ADAPTER.decode(payload))
            encode(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token,
                payload = ConnectResponse.ADAPTER.encode(response)
            )
        }

        OriginalHash.ConnectionService.value to 5u -> {
            rpc.keepAlive(NoData())
            null
        }

        OriginalHash.ConnectionService.value to 7u -> {
            rpc.disconnect(DisconnectRequest.ADAPTER.decode(payload))
            null
        }

        OriginalHash.AuthenticationService.value to 1u -> {
            rpc.logon(LogonRequest.ADAPTER.decode(payload))
            null
        }

        OriginalHash.AuthenticationService.value to 7u -> {
            rpc.verifyWebCredentials(
                VerifyWebCredentialsRequest.ADAPTER.decode(payload)
            )
            null
        }

        else -> {
            logger.warn { "Unknown BNet RPC: service_hash=${header.service_hash}, method_id=${header.method_id}" }
            null
        }
    }

    fun encode(
        serviceId: Int,
        serviceHash: Int,
        methodId: Int,
        token: Int,
        status: Int = 0,
        payload: ByteArray = byteArrayOf(),
    ): ByteArray {
        val header = Header(
            token = token,
            status = status,
            service_id = serviceId,
            service_hash = serviceHash,
            method_id = methodId,
            size = payload.size,
        )

        val headerBytes = Header.ADAPTER.encode(header)

        return buildList {
            add((headerBytes.size shr 8).toByte())
            add(headerBytes.size.toByte())
            addAll(headerBytes.asList())
            addAll(payload.asList())
        }.toByteArray()
    }

    private fun rewind(
        size: UShort,
        header: ByteArray
    ) {
        val tmp = Buffer()
        tmp.writeByte((size.toInt() shr 8).toByte())
        tmp.writeByte(size.toByte())
        tmp.write(header)
        tmp.transferTo(buffer)
    }

    private fun peekU16(buffer: Buffer): UShort? {
        if (buffer.size < 2) return null

        val bytes = buffer.peek().readByteArray(2)
        return (((bytes[0].toInt() and 0xff) shl 8) or
                (bytes[1].toInt() and 0xff))
            .toUShort()
    }
}
