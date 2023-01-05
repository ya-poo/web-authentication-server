package me.yapoo.fido2.handler.dto

data class PublicKeyCredentialParameters(
    val alg: COSEAlgorithmIdentifier,
) {
    val type: PublicKeyCredentialType = PublicKeyCredentialType.PublicKey
}
