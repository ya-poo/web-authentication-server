package me.yapoo.fido2.domain.authentication

import com.webauthn4j.authenticator.Authenticator

data class UserAuthenticator(
    val userId: String,
    val authenticator: Authenticator
)
