package me.yapoo.fido2.domain.authentication

class UserAuthenticationChallengeRepository {

    private val list = mutableListOf<UserAuthenticationChallenge>()

    fun add(
        challenge: UserAuthenticationChallenge
    ) {
        list.removeIf { it.userId == challenge.userId }
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
