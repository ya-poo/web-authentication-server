package me.yapoo.fido2.domain.authentication

class UserAuthenticationChallengeRepository {

    private val list = mutableListOf<UserAuthenticationChallenge>()

    fun add(
        challenge: UserAuthenticationChallenge
    ) {
        list.add(challenge)
    }

    fun find(
        userId: String,
    ): UserAuthenticationChallenge? {
        return list.singleOrNull {
            it.userId == userId
        }
    }
}
