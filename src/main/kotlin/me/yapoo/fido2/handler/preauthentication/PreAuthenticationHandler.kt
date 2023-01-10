package me.yapoo.fido2.handler.preauthentication

import me.yapoo.fido2.config.ServerConfig
import me.yapoo.fido2.domain.authentication.UserAuthenticationChallenge
import me.yapoo.fido2.domain.authentication.UserAuthenticationChallengeRepository
import me.yapoo.fido2.domain.user.UserRepository
import me.yapoo.fido2.handler.dto.UserVerificationRequirement
import java.time.Instant
import java.util.*

class PreAuthenticationHandler(
    private val userRepository: UserRepository,
    private val userAuthenticationChallengeRepository: UserAuthenticationChallengeRepository,
) {

    fun handle(
        request: PreAuthenticationRequest
    ): PreAuthenticationResponse {
        val user = userRepository.find(request.username)
            ?: throw Exception()

        val challenge = UserAuthenticationChallenge(
            userId = user.id,
            challenge = UUID.randomUUID().toString(),
            createdAt = Instant.now(),
        )
        userAuthenticationChallengeRepository.add(challenge)

        return PreAuthenticationResponse(
            challenge = challenge.challenge,
            timeout = challenge.timeout.toMillis().toInt(),
            rpid = ServerConfig.rpid,
            allowCredentials = emptyList(),
            userVerificationRequirement = UserVerificationRequirement.Preferred,
            extensions = emptyMap()
        )
    }
}
