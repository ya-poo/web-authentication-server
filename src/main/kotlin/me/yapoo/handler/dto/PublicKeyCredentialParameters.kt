package me.yapoo.handler.dto

data class PublicKeyCredentialParameters(
    val alg: COSEAlgorithmIdentifier,
) {
    val type: PublicKeyCredentialType = PublicKeyCredentialType.PublicKey
}
