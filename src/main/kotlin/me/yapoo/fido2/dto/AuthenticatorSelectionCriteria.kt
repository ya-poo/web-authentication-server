package me.yapoo.fido2.dto

data class AuthenticatorSelectionCriteria(
    val authenticatorAttachment: AuthenticatorAttachment?,
    val residentKey: ResidentKeyRequirement?,
    val userVerification: UserVerificationRequirement = UserVerificationRequirement.Preferred,
) {
    val requireResidentKey: Boolean = residentKey == ResidentKeyRequirement.Required
}
