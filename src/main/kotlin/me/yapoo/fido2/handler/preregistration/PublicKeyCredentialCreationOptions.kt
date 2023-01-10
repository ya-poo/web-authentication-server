package me.yapoo.fido2.handler.preregistration

import me.yapoo.fido2.handler.dto.AttestationConveyancePreference
import me.yapoo.fido2.handler.dto.AuthenticatorSelectionCriteria
import me.yapoo.fido2.handler.dto.PublicKeyCredentialDescriptor
import me.yapoo.fido2.handler.dto.PublicKeyCredentialParameters
import me.yapoo.fido2.handler.dto.PublicKeyCredentialRpEntity
import me.yapoo.fido2.handler.dto.PublicKeyCredentialUserEntity

typealias PreregistrationResponse = PublicKeyCredentialCreationOptions

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
