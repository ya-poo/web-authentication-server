package me.yapoo.webauthn.dto

import com.upokecenter.cbor.CBORObject


class AttestationObject(
    val fmt: String,
    val attStmt: Map<String, String>,
    val authenticatorData: AuthenticatorData
) {

    companion object {

        fun of(bytes: ByteArray): AttestationObject {
            val cbor = CBORObject.DecodeFromBytes(bytes)
            return AttestationObject(
                fmt = cbor["fmt"].AsString(),
                attStmt = cbor["attStmt"].ToObject(Map::class.java),
                authenticatorData = AuthenticatorData.of(
                    cbor["authData"].ToObject(ByteArray::class.java)
                )
            )
        }
    }
}
