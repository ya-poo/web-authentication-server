package me.yapoo.fido2.domain.authentication

import java.time.Instant
import java.util.UUID

class AuthenticationChallengeRepository {

    private val list = mutableListOf<AuthenticationChallenge>()

    fun add(
        challenge: AuthenticationChallenge
    ) {
        list.removeIf {
            it.sessionId == challenge.sessionId
        }
        list.add(challenge)
    }

    fun find(
        sessionId: UUID,
    ): AuthenticationChallenge? {
        return list.singleOrNull {
            it.sessionId == sessionId &&
                Instant.now() < it.expiresAt
        }
    }
}
