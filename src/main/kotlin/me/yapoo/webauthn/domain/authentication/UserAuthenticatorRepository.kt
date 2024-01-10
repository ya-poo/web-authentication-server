package me.yapoo.webauthn.domain.authentication

class UserAuthenticatorRepository {

    private val list = mutableListOf<UserAuthenticator>()

    fun find(id: ByteArray): UserAuthenticator? {
        return list.firstOrNull {
            it.id.contentEquals(id)
        }
    }

    fun save(authenticatorNew: UserAuthenticator) {
        list.removeIf {
            it.id.contentEquals(authenticatorNew.id)
        }
        list.add(authenticatorNew)
    }
}
