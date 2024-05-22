package me.yapoo.webauthn.domain.authentication

class UserCredentialRepository {

    private val list = mutableListOf<UserCredential>()

    fun find(id: ByteArray): UserCredential? {
        return list.firstOrNull {
            it.id.contentEquals(id)
        }
    }

    fun save(credential: UserCredential) {
        list.removeIf {
            it.id.contentEquals(credential.id)
        }
        list.add(credential)
    }
}
