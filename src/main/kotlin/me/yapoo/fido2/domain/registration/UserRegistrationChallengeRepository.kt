package me.yapoo.fido2.domain.registration

class UserRegistrationChallengeRepository {

    private val list = mutableListOf<UserRegistrationChallenge>()

    fun create(
        challenge: UserRegistrationChallenge
    ) {
        list.add(challenge)
    }

    fun find(
        challenge: String
    ): UserRegistrationChallenge? {
        return list.singleOrNull {
            it.challenge == challenge
        }
    }
}
