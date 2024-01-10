package me.yapoo.webauthn.dto

data class PublicKeyCredentialParameters(
    val alg: COSEAlgorithmIdentifier,
) {
    val type: PublicKeyCredentialType = PublicKeyCredentialType.PublicKey
}
