@file:Suppress("LocalVariableName")

package me.yapoo.fido2.handler.registration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
import me.yapoo.fido2.domain.authentication.UserAuthenticator
import me.yapoo.fido2.domain.authentication.UserAuthenticatorRepository
import me.yapoo.fido2.domain.registration.UserRegistrationChallengeRepository
import me.yapoo.fido2.domain.user.User
import me.yapoo.fido2.domain.user.UserRepository
import me.yapoo.fido2.dto.CollectedClientData
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64
import java.util.UUID

class RegistrationHandler(
    private val userRegistrationChallengeRepository: UserRegistrationChallengeRepository,
    private val userAuthenticatorRepository: UserAuthenticatorRepository,
    private val userRepository: UserRepository,
) {

    private val objectMapper = jacksonObjectMapper()

    fun handle(
        request: RegistrationRequest,
        sessionId: String,
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
            UUID.fromString(sessionId)
        ) ?: throw Exception("registration session に紐づくチャレンジがありません")

        if (serverChallenge.expiresAt <= Instant.now()) {
            throw Exception("timeout")
        }

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

        userAuthenticatorRepository.add(
            UserAuthenticator(
                userId = serverChallenge.userId,
                authenticator = authenticator
            )
        )

        userRepository.add(
            User(
                username = serverChallenge.username,
                displayName = serverChallenge.username,
                id = serverChallenge.userId
            )
        )
    }

    @Suppress("unused")
    fun handle2(
        request: RegistrationRequest,
        sessionId: String,
    ) {
        // step 5
        val jsonText = Base64.getDecoder().decode(request.clientDataJSON)

        // step 6
        val c = objectMapper.readValue<CollectedClientData>(jsonText)

        // step 7
        if (c.type != "webauthn.create") {
            throw Exception("invalid type of CollectedClientData: ${c.type}")
        }

        // step 8
        userRegistrationChallengeRepository.find(UUID.fromString(sessionId))
            ?: throw Exception("invalid challenge of CollectedClientData")

        // step 9
        if (c.origin != ServerConfig.origin) {
            throw Exception("invalid origin of CollectedClientData")
        }

        // step 10
        if (c.topOrigin !== null) {
            // この credential が別 origin の iframe 内で作られて良いことを確認する
            if (c.topOrigin !== "expected value") {
                throw Exception("invalid top Origin: ${c.topOrigin}")
            }
        }

        // step 11
        val hash: ByteArray = MessageDigest.getInstance("SHA-256").digest(jsonText)

        // step 12
    }
}
