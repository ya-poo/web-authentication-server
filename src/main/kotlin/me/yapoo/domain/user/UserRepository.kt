package me.yapoo.domain.user

class UserRepository {

    private val list = listOf(
        User(
            id = "user_id_1234",
            username = "test@example.com",
            displayName = "テスト太郎",
        )
    )

    fun find(
        username: String
    ): User? {
        return list.firstOrNull { it.username == username }
    }
}
