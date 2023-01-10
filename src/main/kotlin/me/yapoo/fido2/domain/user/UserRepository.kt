package me.yapoo.fido2.domain.user

class UserRepository {

    private val list = mutableListOf<User>()

    fun find(
        username: String
    ): User? {
        return list.firstOrNull { it.username == username }
    }

    fun add(
        user: User
    ) {
        list.add(user)
    }
}
