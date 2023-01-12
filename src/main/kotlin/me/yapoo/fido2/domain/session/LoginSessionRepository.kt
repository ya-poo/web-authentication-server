package me.yapoo.fido2.domain.session

class LoginSessionRepository {

    private val list = mutableListOf<LoginSession>()

    fun add(
        session: LoginSession
    ) {
        list.add(session)
    }

    fun find(
        id: String
    ): LoginSession? {
        return list.singleOrNull { it.id == id }
    }
}
