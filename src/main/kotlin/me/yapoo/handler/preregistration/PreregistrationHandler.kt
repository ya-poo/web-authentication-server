package me.yapoo.handler.preregistration

import me.yapoo.domain.metadata.ServerInfo
import me.yapoo.domain.registration.UserRegistrationChallenge
import me.yapoo.domain.registration.UserRegistrationChallengeRepository
import me.yapoo.domain.user.UserRepository
import me.yapoo.handler.dto.AttestationConveyancePreference
import me.yapoo.handler.dto.COSEAlgorithmIdentifier
import me.yapoo.handler.dto.PublicKeyCredentialParameters
import me.yapoo.handler.dto.PublicKeyCredentialRpEntity
import me.yapoo.handler.dto.PublicKeyCredentialUserEntity
import java.time.Instant
import java.util.*

class PreregistrationHandler(
    private val userRepository: UserRepository,
    private val userRegistrationChallengeRepository: UserRegistrationChallengeRepository,
) {

    fun handle(
        request: PreregistrationRequest
    ): PreregistrationResponse {
        if (userRepository.find(request.username) != null) {
            throw Exception()
        }

        val userId = UUID.randomUUID().toString()
        val challenge = UserRegistrationChallenge(
            userId = userId,
            username = request.username,
            challenge = UUID.randomUUID().toString(),
            createdAt = Instant.now()
        )
        userRegistrationChallengeRepository.create(challenge)

        return PreregistrationResponse(
            rp = PublicKeyCredentialRpEntity(
                id = ServerInfo.rpid,
                name = ServerInfo.name
            ),
            user = PublicKeyCredentialUserEntity(
                id = userId,
                name = challenge.username,
                displayName = challenge.username
            ),
            challenge = challenge.challenge,
            pubKeyCredParams = listOf(
                PublicKeyCredentialParameters(
                    alg = COSEAlgorithmIdentifier.ES256,
                )
            ),
            timeout = challenge.timeout.toMillis().toInt(),
            authenticatorSelection = null,
            attestation = AttestationConveyancePreference.None,
            excludeCredentials = emptyList(),
            extensions = emptyMap(),
        )
    }
}
