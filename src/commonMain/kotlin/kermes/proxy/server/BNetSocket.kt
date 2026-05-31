@file:OptIn(ExperimentalSerializationApi::class)

package kermes.proxy.server

import bgs.protocol.account.v1.*
import bgs.protocol.authentication.v1.LogonRequest
import bgs.protocol.authentication.v1.LogonResult
import bgs.protocol.authentication.v1.VerifyWebCredentialsRequest
import bgs.protocol.challenge.v1.ChallengeExternalRequest
import bnet.protocol.*
import bnet.protocol.connection.v1.ConnectRequest
import bnet.protocol.connection.v1.ConnectResponse
import bnet.protocol.connection.v1.DisconnectRequest
import bnet.protocol.game_utilities.v1.ClientRequest
import bnet.protocol.game_utilities.v1.ClientResponse
import bnet.protocol.game_utilities.v1.GetAllValuesForAttributeRequest
import bnet.protocol.game_utilities.v1.GetAllValuesForAttributeResponse
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
import okio.ByteString.Companion.toByteString
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
    suspend fun getAccountState(request: GetAccountStateRequest): GetAccountStateResponse
    suspend fun getGameAccountState(request: GetGameAccountStateRequest): GetGameAccountStateResponse
    suspend fun processClientRequest(request: ClientRequest): ClientResponse
    suspend fun getAllValuesForAttribute(request: GetAllValuesForAttributeRequest): GetAllValuesForAttributeResponse
}

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

class BattleNetRpcImpl(
    private val state: BnetSessionState,
    private val sender: BnetSender,
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

        val endpoint = "127.0.0.1:11118"
        val externalChallenge = ChallengeExternalRequest(
            payload_type = "web_auth_url",
            payload = "https://$endpoint/bnetserver/login/${request.platform}/${request.application_version}/${request.locale}/".encodeToByteArray()
                .toByteString()
        )

        sender.send(
            serviceHash = OriginalHash.ChallengeListener.value,
            methodId = 3u,
            payload = ChallengeExternalRequest.ADAPTER.encode(externalChallenge)
        )
    }

    override suspend fun verifyWebCredentials(
        request: VerifyWebCredentialsRequest
    ) {
        logger.info { "VerifyWebCredentials for client ${state.clientId}" }
        state.isAuthenticated = true
        state.accountId = 1L
        state.sessionKey = ByteArray(64) { 0 }

        val logonResult = LogonResult(
            error_code = 0,
            account_id = EntityId(
                low = 1L,
                high = 72057594037927936L
            ),
            game_account_id = listOf(
                EntityId(
                    low = 1L,
                    high = 144115196671520593L
                )
            ),
            session_key = state.sessionKey!!.toByteString()
        )

        sender.send(
            serviceHash = OriginalHash.AuthenticationListener.value,
            methodId = 5u,
            payload = LogonResult.ADAPTER.encode(logonResult)
        )
    }

    override suspend fun getAccountState(
        request: GetAccountStateRequest
    ): GetAccountStateResponse = GetAccountStateResponse(
        state = AccountState(
            privacy_info = PrivacyInfo(
                is_using_rid = false,
                is_visible_for_view_friends = false,
                is_hidden_from_friend_finder = true
            )
        ),
        tags = AccountFieldTags(
            privacy_info_tag = 3620373325L.toInt()
        )
    )

    override suspend fun getGameAccountState(
        request: GetGameAccountStateRequest
    ): GetGameAccountStateResponse = GetGameAccountStateResponse(
        state = GameAccountState(
            game_level_info = GameLevelInfo(
                name = "Kermes",
                program = 5730135L.toInt()
            ),
            game_status = GameStatus(
                program = 5730135L.toInt()
            )
        ),
        tags = GameAccountFieldTags(
            game_level_info_tag = 1548145795L.toInt(),
            game_status_tag = 2562154393L.toInt()
        )
    )

    override suspend fun processClientRequest(
        request: ClientRequest
    ): ClientResponse {
        val command = request.attribute.find { it.name.startsWith("Command_") }
        logger.debug { "GameUtilitiesService command: ${command?.name}" }

        if (command?.name?.startsWith("Command_RealmListRequest") == true) {
            return ClientResponse(
                attribute = listOf(
                    Attribute(
                        name = "Param_RealmList",
                        value_ = Variant(blob_value = "MockRealmList".encodeToByteArray().toByteString())
                    ),
                    Attribute(
                        name = "Param_CharacterCountList",
                        value_ = Variant(blob_value = "MockCharCount".encodeToByteArray().toByteString())
                    )
                )
            )
        }

        return ClientResponse()
    }

    override suspend fun getAllValuesForAttribute(
        request: GetAllValuesForAttributeRequest
    ): GetAllValuesForAttributeResponse = GetAllValuesForAttributeResponse()
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
            val frame = encodeBnetFrame(
                serviceId = 0,
                serviceHash = serviceHash.toInt(),
                methodId = methodId.toInt(),
                token = actualToken,
                status = status,
                payload = payload
            )
            sendChannel.writeFully(frame)
        }
    }
    val session = BnetSession(
        rpc = BattleNetRpcImpl(state, sender)
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
        // ConnectionService
        OriginalHash.ConnectionService.value to 1u -> {
            val response = rpc.connect(ConnectRequest.ADAPTER.decode(payload))
            encodeBnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token,
                payload = ConnectResponse.ADAPTER.encode(response)
            )
        }

        OriginalHash.ConnectionService.value to 5u -> {
            rpc.keepAlive(NoData())
            encodeBnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token
            )
        }

        OriginalHash.ConnectionService.value to 7u -> {
            rpc.disconnect(DisconnectRequest.ADAPTER.decode(payload))
            encodeBnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token
            )
        }

        // AuthenticationService
        OriginalHash.AuthenticationService.value to 1u -> {
            rpc.logon(LogonRequest.ADAPTER.decode(payload))
            encodeBnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token
            )
        }

        OriginalHash.AuthenticationService.value to 7u -> {
            rpc.verifyWebCredentials(
                VerifyWebCredentialsRequest.ADAPTER.decode(payload)
            )
            encodeBnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token
            )
        }

        // AccountService
        OriginalHash.AccountService.value to 30u -> {
            val response = rpc.getAccountState(
                GetAccountStateRequest.ADAPTER.decode(payload)
            )
            encodeBnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token,
                payload = GetAccountStateResponse.ADAPTER.encode(response)
            )
        }

        OriginalHash.AccountService.value to 31u -> {
            val response = rpc.getGameAccountState(
                GetGameAccountStateRequest.ADAPTER.decode(payload)
            )
            encodeBnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token,
                payload = GetGameAccountStateResponse.ADAPTER.encode(response)
            )
        }

        // GameUtilitiesService
        OriginalHash.GameUtilitiesService.value to 1u -> {
            val response = rpc.processClientRequest(
                ClientRequest.ADAPTER.decode(payload)
            )
            encodeBnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token,
                payload = ClientResponse.ADAPTER.encode(response)
            )
        }

        OriginalHash.GameUtilitiesService.value to 10u -> {
            val response = rpc.getAllValuesForAttribute(
                GetAllValuesForAttributeRequest.ADAPTER.decode(payload)
            )
            encodeBnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token,
                payload = GetAllValuesForAttributeResponse.ADAPTER.encode(response)
            )
        }

        else -> {
            logger.warn { "Unknown BNet RPC: service_hash=${header.service_hash}, method_id=${header.method_id}" }
            encodeBnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token
            )
        }
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

fun encodeBnetFrame(
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
