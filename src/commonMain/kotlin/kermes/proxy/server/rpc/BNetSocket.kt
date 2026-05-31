@file:OptIn(ExperimentalSerializationApi::class)

package kermes.proxy.server.rpc

import bnet.protocol.ProcessId
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kermes.proxy.server.models.BnetFrame
import kermes.proxy.server.models.BnetRpcImpl
import kermes.proxy.server.models.BnetSession
import kotlinx.coroutines.*
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.ExperimentalSerializationApi

private val logger = KotlinLogging.logger {}

interface BnetSender {
    suspend fun send(
        serviceHash: UInt,
        methodId: UInt,
        payload: ByteArray,
        token: Int? = null,
        status: Int = 0
    )
}

class BnetSessionState {
    var clientId: ProcessId? = null
    var isAuthenticated: Boolean = false
    var accountId: Long? = null
    var gameAccounts: List<Long> = emptyList()
    var sessionKey: ByteArray? = null
    var serverTokenCounter: Int = 0
}

suspend fun startBNetTcpServer(
    host: String,
    port: Int
) = coroutineScope {
    val selectorManager = SelectorManager(Dispatchers.Default)
    val serverSocket = aSocket(selectorManager).tcp().bind(host, port)

    logger.info { "BNet Framed TCP Server listening on $host:$port" }

    while (isActive) {
        val socket = serverSocket.accept()
        launch {
            handleBNetConnection(socket)
        }
    }
}

private suspend fun handleBNetConnection(
    socket: Socket
) {
    val remoteAddress = socket.remoteAddress
    logger.debug { "New BNet connection from $remoteAddress" }

    val receiveChannel = socket.openReadChannel()
    val sendChannel = socket.openWriteChannel(autoFlush = true)

    val state = BnetSessionState()
    val sender = object : BnetSender {
        override suspend fun send(
            serviceHash: UInt,
            methodId: UInt,
            payload: ByteArray,
            token: Int?,
            status: Int
        ) {
            val actualToken = token ?: state.serverTokenCounter++
            val frame = BnetFrame(
                serviceId = 0,
                serviceHash = serviceHash.toInt(),
                methodId = methodId.toInt(),
                token = actualToken,
                status = status,
                payload = payload
            )
            sendChannel.writeFully(frame.toByteArray())
        }
    }
    val session = BnetSession(
        rpc = BnetRpcImpl(state, sender)
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
        if (e !is CancellationException) {
            logger.error(e) { "Error in BNet connection $remoteAddress" }
        }
    } finally {
        socket.close()
        logger.debug { "BNet connection $remoteAddress closed" }
    }
}
