package me.yapoo.fido2.domain.authentication

import com.webauthn4j.authenticator.Authenticator

data class UserWebAuthn4jAuthenticator(
    val userId: String,
    val authenticator: Authenticator
)
