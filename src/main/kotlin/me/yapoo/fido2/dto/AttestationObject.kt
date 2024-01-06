package me.yapoo.fido2.dto


class RawAttestationObject(
    val fmt: String,
    val attStmt: Map<String, String>,
    val authData: ByteArray,
)

class AttestationObject(
    val fmt: String,
    val attStmt: Map<String, String>,
    val authenticatorData: AuthenticatorData
)

fun RawAttestationObject.toAttestationObject(): AttestationObject {
    return AttestationObject(
        fmt = fmt,
        attStmt = attStmt,
        authenticatorData = AuthenticatorData.of(authData)
    )
}
