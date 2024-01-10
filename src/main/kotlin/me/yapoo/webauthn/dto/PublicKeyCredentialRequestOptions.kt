package me.yapoo.webauthn.dto

data class PublicKeyCredentialRequestOptions(
    val challenge: String,
    val timeout: Int?,
    val rpid: String?,
    val allowCredentials: List<PublicKeyCredentialDescriptor> = emptyList(),
    val userVerification: UserVerificationRequirement?,
    val extensions: Map<String, Any?>,
)
