package me.yapoo.fido2.handler.dto

data class AuthenticatorSelectionCriteria(
    val authenticatorAttachment: AuthenticatorAttachment?,
    val residentKey: ResidentKeyRequirement?,
    val requireResidentKey: Boolean = false,
    val userVerification: UserVerificationRequirement = UserVerificationRequirement.Preferred,
)
