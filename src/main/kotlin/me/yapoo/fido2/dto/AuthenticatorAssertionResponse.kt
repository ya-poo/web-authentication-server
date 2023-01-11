package me.yapoo.fido2.dto

data class AuthenticatorAssertionResponse(
    val authenticatorData: String,
    val signature: String,
    val userHandle: String,
    val clientDataJSON: String,
)
