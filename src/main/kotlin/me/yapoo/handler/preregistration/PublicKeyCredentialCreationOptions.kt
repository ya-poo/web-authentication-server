package me.yapoo.handler.preregistration

import me.yapoo.handler.dto.AttestationConveyancePreference
import me.yapoo.handler.dto.AuthenticatorSelectionCriteria
import me.yapoo.handler.dto.PublicKeyCredentialDescriptor
import me.yapoo.handler.dto.PublicKeyCredentialParameters
import me.yapoo.handler.dto.PublicKeyCredentialRpEntity
import me.yapoo.handler.dto.PublicKeyCredentialUserEntity

typealias PreregistrationResponse = PublicKeyCredentialCreationOptions

// PreregistrationResponse
data class PublicKeyCredentialCreationOptions(
    val rp: PublicKeyCredentialRpEntity,
    val user: PublicKeyCredentialUserEntity,
    val challenge: String,
    val pubKeyCredParams: List<PublicKeyCredentialParameters>,
    val timeout: Int?,
    val excludeCredentials: List<PublicKeyCredentialDescriptor>,
    val authenticatorSelection: AuthenticatorSelectionCriteria?,
    val attestation: AttestationConveyancePreference = AttestationConveyancePreference.None,
    val extensions: Map<String, Any>,
)
