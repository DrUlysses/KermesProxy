package kermes.proxy.server.models

import bnet.protocol.Header

data class BnetFrame(
    val serviceId: Int,
    val serviceHash: Int,
    val methodId: Int,
    val token: Int,
    val status: Int = 0,
    val payload: ByteArray = byteArrayOf(),
) {
    fun toByteArray(): ByteArray {
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
}
