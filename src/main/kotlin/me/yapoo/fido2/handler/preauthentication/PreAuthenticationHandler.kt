package me.yapoo.fido2.handler.preauthentication

import me.yapoo.fido2.config.ServerConfig
import me.yapoo.fido2.domain.authentication.UserAuthenticationChallenge
import me.yapoo.fido2.domain.authentication.UserAuthenticationChallengeRepository
import me.yapoo.fido2.domain.authentication.UserAuthenticatorRepository
import me.yapoo.fido2.domain.user.UserRepository
import me.yapoo.fido2.dto.PublicKeyCredentialDescriptor
import me.yapoo.fido2.dto.UserVerificationRequirement
import java.time.Instant
import java.util.*

class PreAuthenticationHandler(
    private val userRepository: UserRepository,
    private val userAuthenticationChallengeRepository: UserAuthenticationChallengeRepository,
    private val userAuthenticatorRepository: UserAuthenticatorRepository,
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

        val credentials = userAuthenticatorRepository.findByUserId(user.id)
        if (credentials.isEmpty()) {
            throw Exception("no credentials")
        }

        return PreAuthenticationResponse(
            challenge = challenge.challenge,
            timeout = challenge.timeout.toMillis().toInt(),
            rpid = ServerConfig.rpid,
            allowCredentials = credentials.map {
                val id = Base64.getEncoder().encodeToString(it.authenticator.attestedCredentialData.credentialId)
                PublicKeyCredentialDescriptor(
                    id = id,
                    transports = null
                )
            },
            userVerificationRequirement = UserVerificationRequirement.Preferred,
            extensions = emptyMap()
        )
    }
}
