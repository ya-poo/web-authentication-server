package me.yapoo.fido2.handler.authentication

import com.webauthn4j.WebAuthnAuthenticationManager
import com.webauthn4j.data.AuthenticationParameters
import com.webauthn4j.data.client.Origin
import com.webauthn4j.data.client.challenge.DefaultChallenge
import com.webauthn4j.server.ServerProperty
import com.webauthn4j.util.Base64Util
import com.webauthn4j.validator.exception.ValidationException
import me.yapoo.fido2.config.ServerConfig
import me.yapoo.fido2.domain.authentication.AuthenticationChallengeRepository
import me.yapoo.fido2.domain.authentication.UserAuthenticatorRepository
import me.yapoo.fido2.domain.session.LoginSession
import me.yapoo.fido2.domain.session.LoginSessionRepository
import java.time.Instant
import java.util.UUID

class AuthenticationHandler(
    private val authenticationChallengeRepository: AuthenticationChallengeRepository,
    private val userAuthenticatorRepository: UserAuthenticatorRepository,
    private val loginSessionRepository: LoginSessionRepository,
) {

    fun handle(
        request: AuthenticationRequest,
        sessionId: UUID,
    ): LoginSession {
        val authenticationRequest = com.webauthn4j.data.AuthenticationRequest(
            Base64Util.decode(request.id),
            Base64Util.decode(request.response.userHandle),
            Base64Util.decode(request.response.authenticatorData),
            Base64Util.decode(request.response.clientDataJSON),
            Base64Util.decode(request.response.signature)
        )

        val serverChallenge = authenticationChallengeRepository.find(sessionId)
            ?: throw Exception("challenge was not found")

        if (serverChallenge.expiresAt <= Instant.now()) {
            throw Exception("timeout")
        }

        val userAuthenticator = userAuthenticatorRepository.find(authenticationRequest.credentialId)
            ?: throw Exception()

        val authenticationParameters = AuthenticationParameters(
            ServerProperty(
                Origin.create(ServerConfig.origin),
                ServerConfig.rpid,
                DefaultChallenge(serverChallenge.challenge.toByteArray()),
                null
            ),
            userAuthenticator.authenticator,
            listOf(userAuthenticator.authenticator.attestedCredentialData.credentialId),
            true,
            true,
        )

        val manager = WebAuthnAuthenticationManager()
        try {
            manager.validate(authenticationRequest, authenticationParameters)
        } catch (e: ValidationException) {
            throw Exception(e)
        }

        userAuthenticator.authenticator.counter++
        userAuthenticatorRepository.update(userAuthenticator)

        val session = LoginSession(
            id = UUID.randomUUID().toString(),
            userId = userAuthenticator.userId
        )
        loginSessionRepository.add(session)

        return session
    }
}
