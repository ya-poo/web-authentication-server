package me.yapoo.webauthn.domain.authentication

import java.time.Instant

class AuthenticationChallengeRepository {

    private val list = mutableListOf<AuthenticationChallenge>()

    fun add(
        challenge: AuthenticationChallenge
    ) {
        list.add(challenge)
    }

    fun find(
        challenge: String,
    ): AuthenticationChallenge? {
        return list.singleOrNull {
            it.challenge == challenge &&
                    Instant.now() < it.expiresAt
        }
    }
}
