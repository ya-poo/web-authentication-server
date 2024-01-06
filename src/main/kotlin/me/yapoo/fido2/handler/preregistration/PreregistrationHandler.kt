package me.yapoo.fido2.handler.preregistration

import me.yapoo.fido2.config.ServerConfig
import me.yapoo.fido2.domain.registration.UserRegistrationChallenge
import me.yapoo.fido2.domain.registration.UserRegistrationChallengeRepository
import me.yapoo.fido2.domain.user.UserRepository
import me.yapoo.fido2.dto.AuthenticatorSelectionCriteria
import me.yapoo.fido2.dto.PublicKeyCredentialCreationOptions
import me.yapoo.fido2.dto.PublicKeyCredentialParameters
import me.yapoo.fido2.dto.PublicKeyCredentialRpEntity
import me.yapoo.fido2.dto.PublicKeyCredentialUserEntity
import me.yapoo.fido2.dto.ResidentKeyRequirement
import java.time.Instant
import java.util.UUID

class PreregistrationHandler(
    private val userRepository: UserRepository,
    private val userRegistrationChallengeRepository: UserRegistrationChallengeRepository,
) {

    fun handle(
        request: PreregistrationRequest
    ): Pair<PreregistrationResponse, String> {
        if (userRepository.find(request.username) != null) {
            throw Exception()
        }

        val registrationSession = UUID.randomUUID()
        val userId = UUID.randomUUID().toString()
        val challenge = UserRegistrationChallenge(
            userId = userId,
            username = request.username,
            challenge = UUID.randomUUID().toString(),
            createdAt = Instant.now(),
            registrationSessionId = registrationSession
        )
        userRegistrationChallengeRepository.create(challenge)

        return PreregistrationResponse(
            publicKey = PublicKeyCredentialCreationOptions(
                rp = PublicKeyCredentialRpEntity(
                    id = ServerConfig.rpid,
                    name = ServerConfig.name
                ),
                user = PublicKeyCredentialUserEntity(
                    id = userId,
                    name = challenge.username,
                    displayName = "${challenge.username} (display_name)"
                ),
                challenge = challenge.challenge,
                pubKeyCredParams = ServerConfig.allowedCoseAlgorithms.map {
                    PublicKeyCredentialParameters(alg = it)
                },
                timeout = challenge.timeout.toMillis().toInt(),
                authenticatorSelection = AuthenticatorSelectionCriteria(
                    authenticatorAttachment = null,
                    residentKey = ResidentKeyRequirement.Preferred,
                    userVerification = ServerConfig.userVerificationRequirement
                ),
                attestation = ServerConfig.attestation,
                excludeCredentials = emptyList(),
                extensions = emptyMap(),
            )
        ) to registrationSession.toString()
    }
}
