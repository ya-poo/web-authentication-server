package me.yapoo.webauthn.handler.authentication

import me.yapoo.webauthn.dto.AuthenticatorAssertionResponse

data class AuthenticationRequest(
    val id: String,
    val rawId: String,
    val response: AuthenticatorAssertionResponse
)
