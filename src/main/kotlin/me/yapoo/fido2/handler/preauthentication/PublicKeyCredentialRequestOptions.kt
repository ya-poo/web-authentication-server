package me.yapoo.fido2.handler.preauthentication

import me.yapoo.fido2.handler.dto.PublicKeyCredentialDescriptor
import me.yapoo.fido2.handler.dto.UserVerificationRequirement

typealias PreAuthenticationResponse = PublicKeyCredentialRequestOptions

data class PublicKeyCredentialRequestOptions(
    val challenge: String,
    val timeout: Int?,
    val rpid: String?,
    val allowCredentials: List<PublicKeyCredentialDescriptor>?,
    val userVerificationRequirement: UserVerificationRequirement?,
    val extensions: Map<String, Any?>,
)
