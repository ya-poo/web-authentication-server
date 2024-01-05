package me.yapoo.fido2.domain.registration

import java.time.Duration
import java.time.Instant
import java.util.UUID

data class UserRegistrationChallenge(
    val userId: String,
    val username: String,
    val challenge: String,
    val createdAt: Instant,
    val registrationSessionId: UUID,
) {

    val timeout: Duration = Duration.ofMinutes(5)

    val expiresAt: Instant = createdAt + timeout
}
