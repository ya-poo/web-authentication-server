package me.yapoo.webauthn.handler.preregistration

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import me.yapoo.webauthn.config.ServerConfig
import me.yapoo.webauthn.domain.registration.UserRegistrationChallenge
import me.yapoo.webauthn.domain.registration.UserRegistrationChallengeRepository
import me.yapoo.webauthn.domain.user.UserRepository
import me.yapoo.webauthn.dto.*
import java.time.Instant
import java.util.*

class PreregistrationHandler(
    private val userRepository: UserRepository,
    private val userRegistrationChallengeRepository: UserRegistrationChallengeRepository,
) {

    suspend fun handle(
        call: ApplicationCall,
    ) {
        val request = call.receive<PreregistrationRequest>()

        if (userRepository.find(request.username) != null) {
            throw Exception()
        }

        val userId = UUID.randomUUID().toString()
        val challenge = UserRegistrationChallenge(
            userId = userId,
            username = request.username,
            challenge = UUID.randomUUID().toString(),
            createdAt = Instant.now(),
        )
        userRegistrationChallengeRepository.create(challenge)

        val response = PreregistrationResponse(
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
        )

        call.respond(response)
    }
}
