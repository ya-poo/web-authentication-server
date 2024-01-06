package me.yapoo.fido2.dto

import com.upokecenter.cbor.CBORObject
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and


class RawAttestationObject(
    val fmt: String,
    val attStmt: Map<String, String>,
    val authData: ByteArray,
)

class AttestationObject(
    val fmt: String,
    val attStmt: Map<String, String>,
    val authenticatorData: AuthenticatorData
) {

    class AuthenticatorData(
        val rpidHash: ByteArray,
        val flags: Flags,
        val signCount: Long,
        val attestedCredentialData: AttestedCredentialData,
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
            val credentialPublicKey: CBORObject
        )
    }
}

fun RawAttestationObject.toAttestationObject(): AttestationObject {
    val rpidHash = authData.slice(0 until 32).toByteArray()
    val rawFlags = authData[32]
    val flags = AttestationObject.AuthenticatorData.Flags(
        up = rawFlags.and(0b00000001.toByte()) == (0b00000001).toByte(),
        uv = rawFlags.and(0b00000100.toByte()) == (0b00000100).toByte(),
        be = rawFlags.and(0b00001000.toByte()) == (0b00001000).toByte(),
        bs = rawFlags.and(0b00010000.toByte()) == (0b00010000).toByte(),
        at = rawFlags.and(0b01000000.toByte()) == (0b01000000).toByte(),
        ed = rawFlags.and(0b10000000.toByte()) == (0b10000000).toByte(),
    )
    val signCount = authData.slice(33 until 37).let {
        // 通常の Long としてパースするため、先頭 4 バイトを 0 で埋める
        ByteBuffer.wrap(byteArrayOf(0, 0, 0, 0, it[0], it[1], it[2], it[3])).order(ByteOrder.BIG_ENDIAN)
            .getLong()
    }

    val stream: ByteArrayInputStream

    val attestedCredentialData = if (flags.at) {
        val aaguid = authData.slice(37 until 53).toByteArray()
        val l = authData.slice(53 until 55).let {
            // 通常の int としてパースするため、先頭 2 バイトを 0 で埋める
            ByteBuffer.wrap(byteArrayOf(0, 0, it[0], it[1])).order(ByteOrder.BIG_ENDIAN).getInt()
        }
        val credentialId = authData.slice(55 until (55 + l)).toByteArray()
        stream = ByteArrayInputStream(authData.slice(55 + l until authData.size).toByteArray())
        val credentialPublicKey = CBORObject.Read(stream)
        AttestationObject.AuthenticatorData.AttestedCredentialData(
            aaguid = aaguid,
            credentialIdLength = l,
            credentialId = credentialId,
            credentialPublicKey = credentialPublicKey
        )
    } else {
        throw Exception("Attested Credential Data is null")
    }

    val extensions = if (flags.ed) {
        if (stream.available() == 0) {
            throw Exception("failed to parse extension data")
        }
        CBORObject.Read(stream)
    } else null

    return AttestationObject(
        fmt = fmt,
        attStmt = attStmt,
        authenticatorData = AttestationObject.AuthenticatorData(
            rpidHash = rpidHash,
            flags = flags,
            signCount = signCount,
            attestedCredentialData = attestedCredentialData,
            extensions = extensions
        )
    )
}
