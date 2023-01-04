package me.yapoo.domain.registration

class UserRegistrationChallengeRepository {

    private val list = mutableListOf<UserRegistrationChallenge>()

    fun create(
        challenge: UserRegistrationChallenge
    ) {
        list.add(challenge)
    }
}
