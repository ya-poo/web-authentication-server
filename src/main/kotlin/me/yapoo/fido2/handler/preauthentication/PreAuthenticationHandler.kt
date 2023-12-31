package me.yapoo.fido2.handler.preauthentication

import me.yapoo.fido2.config.ServerConfig
import me.yapoo.fido2.domain.authentication.AuthenticationChallenge
import me.yapoo.fido2.domain.authentication.AuthenticationChallengeRepository
import me.yapoo.fido2.dto.UserVerificationRequirement
import java.time.Instant
import java.util.UUID

class PreAuthenticationHandler(
    private val authenticationChallengeRepository: AuthenticationChallengeRepository,
) {

    fun handle(): Pair<PreAuthenticationResponse, String> {
        val sessionId = UUID.randomUUID()
        val challenge = AuthenticationChallenge(
            challenge = UUID.randomUUID().toString(), createdAt = Instant.now(), sessionId = sessionId
        )
        authenticationChallengeRepository.add(challenge)

        return PreAuthenticationResponse(
            challenge = challenge.challenge,
            timeout = challenge.timeout.toMillis().toInt(),
            rpid = ServerConfig.rpid,
            allowCredentials = emptyList(),
            userVerificationRequirement = UserVerificationRequirement.Preferred,
            extensions = emptyMap()
        ) to sessionId.toString()
    }
}
