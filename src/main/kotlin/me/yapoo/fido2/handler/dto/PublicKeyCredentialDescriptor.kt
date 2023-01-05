package me.yapoo.fido2.handler.dto

data class PublicKeyCredentialDescriptor(
    val id: String,
    val transports: AuthenticatorTransport?,
) {
    val type: PublicKeyCredentialType = PublicKeyCredentialType.PublicKey
}
