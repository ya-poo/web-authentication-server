package me.yapoo.webauthn.handler.preregistration

import io.ktor.http.Cookie
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import me.yapoo.webauthn.config.ServerConfig
import me.yapoo.webauthn.domain.registration.UserRegistrationChallenge
import me.yapoo.webauthn.domain.registration.UserRegistrationChallengeRepository
import me.yapoo.webauthn.domain.user.UserRepository
import me.yapoo.webauthn.dto.AuthenticatorSelectionCriteria
import me.yapoo.webauthn.dto.PublicKeyCredentialCreationOptions
import me.yapoo.webauthn.dto.PublicKeyCredentialParameters
import me.yapoo.webauthn.dto.PublicKeyCredentialRpEntity
import me.yapoo.webauthn.dto.PublicKeyCredentialUserEntity
import me.yapoo.webauthn.dto.ResidentKeyRequirement
import java.time.Instant
import java.util.UUID

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

        call.response.cookies.append(
            Cookie(
                name = "registration-session",
                value = registrationSession.toString()
            )
        )
        call.respond(response)
    }
}
