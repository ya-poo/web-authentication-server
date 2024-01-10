package me.yapoo.webauthn.domain.registration

import java.util.UUID

class UserRegistrationChallengeRepository {

    private val list = mutableListOf<UserRegistrationChallenge>()

    fun create(
        challenge: UserRegistrationChallenge
    ) {
        list.removeIf { it.userId == challenge.userId }
        list.add(challenge)
    }

    fun find(
        sessionId: UUID
    ): UserRegistrationChallenge? {
        return list.singleOrNull {
            it.registrationSessionId == sessionId
        }
    }
}
