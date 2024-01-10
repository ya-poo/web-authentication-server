package me.yapoo.webauthn.dto

import com.upokecenter.cbor.CBORObject
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and

class AuthenticatorData(
    val rpidHash: ByteArray,
    val flags: Flags,
    val signCount: Long,
    val attestedCredentialData: AttestedCredentialData?,
    val extensions: CBORObject?
) {

    class Flags(
        // User Present
        val up: Boolean,
        // User Verified
        val uv: Boolean,
        // Backup Eligibility
        val be: Boolean,
        // Backup State
        val bs: Boolean,
        // Attested credential data included
        val at: Boolean,
        // Extension data included
        val ed: Boolean
    )

    class AttestedCredentialData(
        val aaguid: ByteArray,
        val credentialIdLength: Int,
        val credentialId: ByteArray,
        val credentialPublicKey: CredentialPublicKey
    )

    companion object {

        fun of(bytes: ByteArray): AuthenticatorData {
            val rpidHash = bytes.slice(0 until 32).toByteArray()
            val rawFlags = bytes[32]
            val flags = Flags(
                up = rawFlags.and(0b00000001.toByte()) == (0b00000001).toByte(),
                uv = rawFlags.and(0b00000100.toByte()) == (0b00000100).toByte(),
                be = rawFlags.and(0b00001000.toByte()) == (0b00001000).toByte(),
                bs = rawFlags.and(0b00010000.toByte()) == (0b00010000).toByte(),
                at = rawFlags.and(0b01000000.toByte()) == (0b01000000).toByte(),
                ed = rawFlags.and(0b10000000.toByte()) == (0b10000000).toByte(),
            )
            val signCount = bytes.slice(33 until 37).let {
                // 通常の Long としてパースするため、先頭 4 バイトを 0 で埋める
                ByteBuffer.wrap(byteArrayOf(0, 0, 0, 0, it[0], it[1], it[2], it[3])).order(ByteOrder.BIG_ENDIAN)
                    .getLong()
            }

            var stream: ByteArrayInputStream? = null

            val attestedCredentialData = if (flags.at) {
                val aaguid = bytes.slice(37 until 53).toByteArray()
                val l = bytes.slice(53 until 55).let {
                    // 通常の int としてパースするため、先頭 2 バイトを 0 で埋める
                    ByteBuffer.wrap(byteArrayOf(0, 0, it[0], it[1])).order(ByteOrder.BIG_ENDIAN).getInt()
                }
                val credentialId = bytes.slice(55 until (55 + l)).toByteArray()
                stream = ByteArrayInputStream(bytes.slice(55 + l until bytes.size).toByteArray())
                val credentialPublicKey = CBORObject.Read(stream)
                AttestedCredentialData(
                    aaguid = aaguid,
                    credentialIdLength = l,
                    credentialId = credentialId,
                    credentialPublicKey = CredentialPublicKey.of(credentialPublicKey)
                )
            } else {
                null
            }

            val extensions = if (flags.ed) {
                if (stream == null) {
                    stream = ByteArrayInputStream(bytes.slice(37 until bytes.size).toByteArray())
                }
                if (stream.available() == 0) {
                    throw Exception("failed to parse extension data")
                }
                CBORObject.Read(stream)
            } else null

            return AuthenticatorData(
                rpidHash = rpidHash,
                flags = flags,
                signCount = signCount,
                attestedCredentialData = attestedCredentialData,
                extensions = extensions
            )
        }
    }
}
