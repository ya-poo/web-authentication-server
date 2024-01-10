package me.yapoo.webauthn.handler.preauthentication

import io.ktor.http.Cookie
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import me.yapoo.webauthn.config.ServerConfig
import me.yapoo.webauthn.domain.authentication.AuthenticationChallenge
import me.yapoo.webauthn.domain.authentication.AuthenticationChallengeRepository
import me.yapoo.webauthn.dto.PublicKeyCredentialRequestOptions
import java.time.Instant
import java.util.UUID

class PreAuthenticationHandler(
    private val authenticationChallengeRepository: AuthenticationChallengeRepository,
) {

    suspend fun handle(
        call: ApplicationCall
    ) {
        val sessionId = UUID.randomUUID()
        val challenge = AuthenticationChallenge(
            challenge = UUID.randomUUID().toString(),
            createdAt = Instant.now(),
            sessionId = sessionId
        )
        authenticationChallengeRepository.add(challenge)

        val response = PreAuthenticationResponse(
            publicKey = PublicKeyCredentialRequestOptions(
                challenge = challenge.challenge,
                timeout = challenge.timeout.toMillis().toInt(),
                rpid = ServerConfig.rpid,
                allowCredentials = emptyList(),
                userVerification = ServerConfig.userVerificationRequirement,
                extensions = emptyMap()
            ),
        )

        call.response.cookies.append(
            Cookie(
                name = "authentication-session",
                value = sessionId.toString()
            )
        )
        call.respond(response)
    }
}
