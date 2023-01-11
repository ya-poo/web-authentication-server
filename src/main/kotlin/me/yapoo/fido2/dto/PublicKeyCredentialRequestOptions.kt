package me.yapoo.fido2.dto

data class PublicKeyCredentialRequestOptions(
    val challenge: String,
    val timeout: Int?,
    val rpid: String?,
    val allowCredentials: List<PublicKeyCredentialDescriptor>?,
    val userVerificationRequirement: UserVerificationRequirement?,
    val extensions: Map<String, Any?>,
)
