package me.yapoo.webauthn.dto

data class AuthenticatorSelectionCriteria(
    val authenticatorAttachment: AuthenticatorAttachment?,
    val residentKey: ResidentKeyRequirement?,
    val userVerification: UserVerificationRequirement = UserVerificationRequirement.Preferred,
) {
    val requireResidentKey: Boolean = residentKey == ResidentKeyRequirement.Required
}
