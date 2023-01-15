package me.yapoo.fido2.domain.authentication

class UserAuthenticatorRepository {

    private val list = mutableListOf<UserAuthenticator>()

    fun add(
        authenticator: UserAuthenticator
    ) {
        list.add(authenticator)
    }

    fun find(
        credentialId: ByteArray,
    ): UserAuthenticator? {
        return list.singleOrNull {
            it.authenticator.attestedCredentialData.credentialId.contentEquals(credentialId)
        }
    }

    fun findByUserId(
        userId: String,
    ): List<UserAuthenticator> {
        return list.filter { it.userId == userId }
    }

    fun update(
        authenticator: UserAuthenticator
    ) {
        list.removeIf {
            it.authenticator.attestedCredentialData.credentialId.contentEquals(authenticator.authenticator.attestedCredentialData.credentialId) &&
                    it.userId == authenticator.userId
        }
        list.add(authenticator)
    }
}
