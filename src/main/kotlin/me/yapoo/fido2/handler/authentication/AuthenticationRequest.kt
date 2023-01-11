package me.yapoo.fido2.handler.authentication

import me.yapoo.fido2.dto.AuthenticatorAssertionResponse

data class AuthenticationRequest(
    val id: String,
    val rawId: String,
    val response: AuthenticatorAssertionResponse
)
