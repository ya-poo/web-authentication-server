package me.yapoo.fido2.domain.authentication

import com.webauthn4j.authenticator.Authenticator

class AuthenticatorRepository {

    private val list = mutableListOf<Authenticator>()

    fun add(
        authenticator: Authenticator
    ) {
        list.add(authenticator)
    }
}
