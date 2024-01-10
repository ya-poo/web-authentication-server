package me.yapoo.webauthn.domain.authentication

import com.webauthn4j.authenticator.Authenticator

data class UserWebAuthn4jAuthenticator(
    val userId: String,
    val authenticator: Authenticator
)
