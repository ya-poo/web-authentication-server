package me.yapoo.fido2.domain.user

class UserRepository {

    private val list = mutableListOf<User>()

    fun find(
        username: String
    ): User? {
        return list.singleOrNull { it.username == username }
    }

    fun findById(
        id: String
    ): User? {
        return list.singleOrNull { it.id == id }
    }

    fun add(
        user: User
    ) {
        list.add(user)
    }
}
