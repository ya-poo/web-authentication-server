package me.yapoo.webauthn.dto

data class PublicKeyCredentialDescriptor(
    val id: String,
    val transports: AuthenticatorTransport?,
) {
    val type: PublicKeyCredentialType = PublicKeyCredentialType.PublicKey
}
