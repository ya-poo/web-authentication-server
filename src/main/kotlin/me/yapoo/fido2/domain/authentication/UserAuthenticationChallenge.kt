package me.yapoo.fido2.domain.authentication

import java.time.Duration
import java.time.Instant
import java.util.UUID

data class UserAuthenticationChallenge(
    val challenge: String,
    val createdAt: Instant,
    val sessionId: UUID,
) {

    val timeout: Duration = Duration.ofMinutes(5)

    val expiresAt: Instant = createdAt + timeout
}
