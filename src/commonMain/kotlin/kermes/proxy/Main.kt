package kermes.proxy

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kermes.proxy.config.loadAndVerifyConfig
import kermes.proxy.server.bNetRest
import kermes.proxy.server.realmSocket
import kermes.proxy.server.rpc.startBNetTcpServer
import kermes.proxy.server.worldSocket
import kermes.proxy.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.files.Path
import uniffi.kermes_proxy.startRawProxyWithPfx
import uniffi.kermes_proxy.startReverseProxyWithPfx

private val logger = KotlinLogging.logger {}

const val LOCALHOST = "127.0.0.1"

fun main(arguments: Array<String>) {
    runBlocking {
        coroutineScope {
            val parsedArguments = parseArguments(arguments)

            workingDir = Path(parsedArguments[Argument.WorkDir] ?: ".")

            initFileLogging()

            val config = loadAndVerifyConfig(
                configPath = workingDir + Path(parsedArguments[Argument.ConfigPath] ?: "HermesProxy.config")
            )

            val bNetSocketBackendPort = config.bNetPort + 10_000
            val bNetRestBackendPort = config.restPort + 10_000
            val realmBackendPort = config.realmPort + 10_000
            val worldBackendPort = config.instancePort + 10_000

            launch {
                runCatching {
                    startBNetTcpServer(
                        host = LOCALHOST,
                        port = bNetSocketBackendPort
                    )
                }.getOrElse {
                    logger.error(it) { "Failed to start BNetSocket TCP server" }
                }
            }

            runCatching {
                embeddedServer(
                    factory = CIO,
                    host = LOCALHOST,
                    port = bNetRestBackendPort,
                    module = { bNetRest() }
                ).startSuspend()
            }.getOrElse {
                logger.error(it) { "Failed to start BNetRest server" }
            }

            runCatching {
                embeddedServer(
                    factory = CIO,
                    host = LOCALHOST,
                    port = realmBackendPort,
                    module = { realmSocket() }
                ).startSuspend()
            }.getOrElse {
                logger.error(it) { "Failed to start realmSocket server" }
            }

            runCatching {
                embeddedServer(
                    factory = CIO,
                    host = LOCALHOST,
                    port = worldBackendPort,
                    module = { worldSocket() }
                ).startSuspend()
            }.getOrElse {
                logger.error(it) { "Failed to start worldSocket server" }
            }

            val pfxPath = resPathByNameOrNull(
                fileName = "files/BNetServer.pfx"
            )?.toAbsolutePathString() ?: run {
                val message = "Can't find files/BNetServer.pfx in resource folder"
                logger.error { message }
                error(message)
            }
            
            launch(Dispatchers.Default) {
                startRawProxyWithPfx(
                    pfxPath = pfxPath,
                    inputPort = config.bNetPort.toUShort(),
                    exportPort = bNetSocketBackendPort.toUShort()
                )
            }

            launch(Dispatchers.Default) {
                startReverseProxyWithPfx(
                    pfxPath = pfxPath,
                    inputPort = config.restPort.toUShort(),
                    exportPort = bNetRestBackendPort.toUShort()
                )
            }

            launch(Dispatchers.Default) {
                startReverseProxyWithPfx(
                    pfxPath = pfxPath,
                    inputPort = config.realmPort.toUShort(),
                    exportPort = realmBackendPort.toUShort()
                )
            }

            launch(Dispatchers.Default) {
                startReverseProxyWithPfx(
                    pfxPath = pfxPath,
                    inputPort = config.instancePort.toUShort(),
                    exportPort = worldBackendPort.toUShort()
                )
            }
        }
    }
}
