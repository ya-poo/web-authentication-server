package me.yapoo.fido2.domain.authentication

import java.util.UUID

class UserAuthenticationChallengeRepository {

    private val list = mutableListOf<UserAuthenticationChallenge>()

    fun add(
        challenge: UserAuthenticationChallenge
    ) {
        list.removeIf {
            it.sessionId == challenge.sessionId
        }
        list.add(challenge)
    }

    fun find(
        sessionId: UUID,
    ): UserAuthenticationChallenge? {
        return list.singleOrNull {
            it.sessionId == sessionId
        }
    }
}
