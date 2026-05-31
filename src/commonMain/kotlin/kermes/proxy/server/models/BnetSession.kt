package kermes.proxy.server.models

import bgs.protocol.account.v1.GetAccountStateRequest
import bgs.protocol.account.v1.GetAccountStateResponse
import bgs.protocol.account.v1.GetGameAccountStateRequest
import bgs.protocol.account.v1.GetGameAccountStateResponse
import bgs.protocol.authentication.v1.LogonRequest
import bgs.protocol.authentication.v1.VerifyWebCredentialsRequest
import bnet.protocol.Header
import bnet.protocol.NoData
import bnet.protocol.connection.v1.ConnectRequest
import bnet.protocol.connection.v1.ConnectResponse
import bnet.protocol.connection.v1.DisconnectRequest
import bnet.protocol.game_utilities.v1.ClientRequest
import bnet.protocol.game_utilities.v1.ClientResponse
import bnet.protocol.game_utilities.v1.GetAllValuesForAttributeRequest
import bnet.protocol.game_utilities.v1.GetAllValuesForAttributeResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.io.Buffer
import kotlinx.io.readByteArray

private val logger = KotlinLogging.logger {}

class BnetSession(
    private val rpc: BnetRpc
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
                responses += dispatch(header, payload)
            } else {
                logger.debug { "Skipping special/ping service frame: id=${header.service_id}, hash=${header.service_hash}" }
            }
        }
    }

    private suspend fun dispatch(
        header: Header,
        payload: ByteArray,
    ): ByteArray = when (header.service_hash?.toUInt() to header.method_id?.toUInt()) {
        // ConnectionService
        OriginalHash.ConnectionService.value to 1u -> {
            val response = rpc.connect(ConnectRequest.ADAPTER.decode(payload))
            BnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token,
                payload = ConnectResponse.ADAPTER.encode(response)
            )
        }

        OriginalHash.ConnectionService.value to 5u -> {
            rpc.keepAlive(NoData())
            BnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token
            )
        }

        OriginalHash.ConnectionService.value to 7u -> {
            rpc.disconnect(DisconnectRequest.ADAPTER.decode(payload))
            BnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token
            )
        }

        // AuthenticationService
        OriginalHash.AuthenticationService.value to 1u -> {
            rpc.logon(LogonRequest.ADAPTER.decode(payload))
            BnetFrame(
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
            BnetFrame(
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
            BnetFrame(
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
            BnetFrame(
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
            BnetFrame(
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
            BnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token,
                payload = GetAllValuesForAttributeResponse.ADAPTER.encode(response)
            )
        }

        else -> {
            logger.warn { "Unknown BNet RPC: service_hash=${header.service_hash}, method_id=${header.method_id}" }
            BnetFrame(
                serviceId = header.service_id,
                serviceHash = header.service_hash ?: 0,
                methodId = header.method_id ?: 0,
                token = header.token
            )
        }
    }.toByteArray()

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
