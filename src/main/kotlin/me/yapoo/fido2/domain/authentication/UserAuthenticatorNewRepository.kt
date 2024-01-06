package me.yapoo.fido2.domain.authentication

class UserAuthenticatorNewRepository {

    private val list = mutableListOf<UserAuthenticatorNew>()

    fun find(id: ByteArray): UserAuthenticatorNew? {
        return list.firstOrNull {
            it.id.contentEquals(id)
        }
    }

    fun add(authenticatorNew: UserAuthenticatorNew) {
        list.add(authenticatorNew)
    }
}
