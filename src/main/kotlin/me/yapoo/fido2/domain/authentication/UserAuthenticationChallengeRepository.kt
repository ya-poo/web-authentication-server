package me.yapoo.fido2.domain.authentication

class UserAuthenticationChallengeRepository {

    private val list = mutableListOf<UserAuthenticationChallenge>()

    fun add(
        challenge: UserAuthenticationChallenge
    ) {
        list.add(challenge)
    }
}
