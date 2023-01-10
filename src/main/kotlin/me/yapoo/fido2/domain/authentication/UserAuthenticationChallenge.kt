package me.yapoo.fido2.domain.authentication

import java.time.Duration
import java.time.Instant

data class UserAuthenticationChallenge(
    val userId: String,
    val challenge: String,
    val createdAt: Instant
) {

    val timeout: Duration = Duration.ofMinutes(5)
}
