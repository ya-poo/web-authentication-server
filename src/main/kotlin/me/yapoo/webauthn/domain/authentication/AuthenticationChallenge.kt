package me.yapoo.webauthn.domain.authentication

import java.time.Duration
import java.time.Instant

data class AuthenticationChallenge(
    val challenge: String,
    val createdAt: Instant,
) {

    val timeout: Duration = Duration.ofMinutes(5)

    val expiresAt: Instant = createdAt + timeout
}
