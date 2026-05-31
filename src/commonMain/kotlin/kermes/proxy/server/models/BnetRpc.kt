package kermes.proxy.server.models

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
import kermes.proxy.server.rpc.BnetSender
import kermes.proxy.server.rpc.BnetSessionState
import kotlinx.rpc.annotations.Rpc
import okio.ByteString.Companion.toByteString
import platform.posix.getpid
import kotlin.time.Clock

private val logger = KotlinLogging.logger {}

@Rpc
sealed interface BnetRpc {
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

class BnetRpcImpl(
    private val state: BnetSessionState,
    private val sender: BnetSender,
) : BnetRpc {
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
