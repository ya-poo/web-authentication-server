package me.yapoo.fido2.domain.authentication

class UserWebAuthn4jAuthenticatorRepository {

    private val list = mutableListOf<UserWebAuthn4jAuthenticator>()

    fun add(
        authenticator: UserWebAuthn4jAuthenticator
    ) {
        list.add(authenticator)
    }

    fun find(
        credentialId: ByteArray,
    ): UserWebAuthn4jAuthenticator? {
        return list.singleOrNull {
            it.authenticator.attestedCredentialData.credentialId.contentEquals(credentialId)
        }
    }

    fun findByUserId(
        userId: String,
    ): List<UserWebAuthn4jAuthenticator> {
        return list.filter { it.userId == userId }
    }

    fun update(
        authenticator: UserWebAuthn4jAuthenticator
    ) {
        list.removeIf {
            it.authenticator.attestedCredentialData.credentialId.contentEquals(authenticator.authenticator.attestedCredentialData.credentialId) &&
                    it.userId == authenticator.userId
        }
        list.add(authenticator)
    }
}
