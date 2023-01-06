@file:Suppress("LocalVariableName")

package me.yapoo.fido2.handler.registration

import com.webauthn4j.WebAuthnRegistrationManager
import com.webauthn4j.authenticator.AuthenticatorImpl
import com.webauthn4j.data.PublicKeyCredentialParameters
import com.webauthn4j.data.PublicKeyCredentialType
import com.webauthn4j.data.RegistrationParameters
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier
import com.webauthn4j.data.client.Origin
import com.webauthn4j.data.client.challenge.DefaultChallenge
import com.webauthn4j.server.ServerProperty
import com.webauthn4j.util.Base64Util
import com.webauthn4j.validator.exception.ValidationException
import me.yapoo.fido2.config.ServerConfig
import me.yapoo.fido2.domain.authentication.AuthenticatorRepository
import me.yapoo.fido2.domain.registration.UserRegistrationChallengeRepository
import java.util.*

class RegistrationHandler(
    private val userRegistrationChallengeRepository: UserRegistrationChallengeRepository,
    private val authenticatorRepository: AuthenticatorRepository
) {

    fun handle(
        request: RegistrationRequest
    ) {
        val manager = WebAuthnRegistrationManager.createNonStrictWebAuthnRegistrationManager()

        val registrationRequest = com.webauthn4j.data.RegistrationRequest(
            Base64Util.decode(request.attestationObject),
            Base64Util.decode(request.clientDataJSON)
        )
        val registrationData = manager.parse(registrationRequest)
        if (registrationData.collectedClientData == null) {
            throw Exception()
        }

        val serverChallenge = userRegistrationChallengeRepository.find(
            String(registrationData.collectedClientData!!.challenge.value)
        ) ?: throw Exception()

        val registrationParameters = RegistrationParameters(
            ServerProperty(
                Origin.create(ServerConfig.origin),
                ServerConfig.rpid,
                DefaultChallenge(serverChallenge.challenge.toByteArray()),
                null
            ),
            listOf(
                PublicKeyCredentialParameters(
                    PublicKeyCredentialType.PUBLIC_KEY,
                    COSEAlgorithmIdentifier.ES256
                )
            ),
            false,
            true
        )

        try {
            manager.validate(registrationData, registrationParameters)
        } catch (e: ValidationException) {
            throw Exception(e)
        }

        val authenticator = AuthenticatorImpl(
            registrationData.attestationObject!!.authenticatorData.attestedCredentialData!!,
            registrationData.attestationObject?.attestationStatement,
            registrationData.attestationObject!!.authenticatorData.signCount
        )

        authenticatorRepository.add(authenticator)
    }
}
