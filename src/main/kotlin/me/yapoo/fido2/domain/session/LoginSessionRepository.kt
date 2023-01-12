package me.yapoo.fido2.domain.session

class LoginSessionRepository {

    private val list = mutableListOf<LoginSession>()

    fun add(
        session: LoginSession
    ) {
        list.add(session)
    }
}
