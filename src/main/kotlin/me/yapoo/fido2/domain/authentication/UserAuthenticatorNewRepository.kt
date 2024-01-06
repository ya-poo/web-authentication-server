package me.yapoo.fido2.domain.authentication

class UserAuthenticatorNewRepository {

    private val list = mutableListOf<UserAuthenticatorNew>()

    fun find(id: ByteArray): UserAuthenticatorNew? {
        return list.firstOrNull {
            it.id.contentEquals(id)
        }
    }

    fun save(authenticatorNew: UserAuthenticatorNew) {
        list.removeIf {
            it.id.contentEquals(authenticatorNew.id)
        }
        list.add(authenticatorNew)
    }
}
