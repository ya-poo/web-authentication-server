package me.yapoo.fido2.handler.preauthentication

import me.yapoo.fido2.config.ServerConfig
import me.yapoo.fido2.domain.authentication.UserAuthenticationChallenge
import me.yapoo.fido2.domain.authentication.UserAuthenticationChallengeRepository
import me.yapoo.fido2.dto.UserVerificationRequirement
import java.time.Instant
import java.util.UUID

class PreAuthenticationHandler(
    private val userAuthenticationChallengeRepository: UserAuthenticationChallengeRepository,
) {

    fun handle(): Pair<PreAuthenticationResponse, String> {
        val sessionId = UUID.randomUUID()
        val challenge = UserAuthenticationChallenge(
            challenge = UUID.randomUUID().toString(), createdAt = Instant.now(), sessionId = sessionId
        )
        userAuthenticationChallengeRepository.add(challenge)

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
