package me.yapoo.fido2.dto

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
