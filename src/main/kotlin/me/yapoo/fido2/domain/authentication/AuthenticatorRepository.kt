package me.yapoo.fido2.domain.authentication

import com.webauthn4j.authenticator.Authenticator

class AuthenticatorRepository {

    private val list = mutableListOf<Authenticator>()

    fun add(
        authenticator: Authenticator
    ) {
        list.add(authenticator)
    }

    fun find(
        credentialId: ByteArray,
    ): Authenticator? {
        return list.singleOrNull {
            it.attestedCredentialData.credentialId.contentEquals(credentialId)
        }
    }

    fun update(
        authenticator: Authenticator
    ) {
        list.removeIf {
            it.attestedCredentialData.credentialId.contentEquals(authenticator.attestedCredentialData.credentialId)
        }
        list.add(authenticator)
    }
}
