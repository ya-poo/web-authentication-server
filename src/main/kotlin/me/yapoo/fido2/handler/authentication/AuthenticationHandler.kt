package me.yapoo.fido2.handler.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.webauthn4j.WebAuthnAuthenticationManager
import com.webauthn4j.data.AuthenticationParameters
import com.webauthn4j.data.client.Origin
import com.webauthn4j.data.client.challenge.DefaultChallenge
import com.webauthn4j.server.ServerProperty
import com.webauthn4j.util.Base64Util
import com.webauthn4j.validator.exception.ValidationException
import me.yapoo.fido2.config.ServerConfig
import me.yapoo.fido2.domain.authentication.AuthenticationChallengeRepository
import me.yapoo.fido2.domain.authentication.UserAuthenticatorNewRepository
import me.yapoo.fido2.domain.authentication.UserAuthenticatorRepository
import me.yapoo.fido2.domain.session.LoginSession
import me.yapoo.fido2.domain.session.LoginSessionRepository
import me.yapoo.fido2.domain.user.UserRepository
import me.yapoo.fido2.dto.AuthenticatorData
import me.yapoo.fido2.dto.CollectedClientData
import me.yapoo.fido2.dto.UserVerificationRequirement
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64
import java.util.UUID

class AuthenticationHandler(
    private val authenticationChallengeRepository: AuthenticationChallengeRepository,
    private val userAuthenticatorRepository: UserAuthenticatorRepository,
    private val loginSessionRepository: LoginSessionRepository,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
    private val userAuthenticatorNewRepository: UserAuthenticatorNewRepository,
) {
    fun handle(
        request: AuthenticationRequest,
        sessionId: String,
    ): LoginSession {
        /*
         * Verifying an Authentication Assertion
         * https://www.w3.org/TR/webauthn-3/#sctn-verifying-assertion
         */

        // step 5: skip (allowCredentials is empty)
        // If options.allowCredentials is not empty,
        // verify that credential.id identifies one of the public key credentials listed in options.allowCredentials.

        // step 6
        // Identify the user being authenticated and let credentialRecord be the credential record for the credential:
        val userId = Base64.getDecoder().decode(request.response.userHandle).toString(Charsets.UTF_8)
        val user = userRepository.findById(userId)
            ?: throw Exception("user not found: $userId")

        // step 9
        // Let cData, authData and sig denote the value of response’s clientDataJSON, authenticatorData, and signature respectively.
        val cData = Base64.getDecoder().decode(request.response.clientDataJSON)
        val authData = AuthenticatorData.of(Base64.getDecoder().decode(request.response.authenticatorData))
        val sig = Base64.getDecoder().decode(request.response.signature)

        // step 10
        // Let JSONtext be the result of running UTF-8 decode on the value of cData.
        val jsonText = String(cData, Charsets.UTF_8)

        // step 11
        // Let C, the client data claimed as used for the signature,
        // be the result of running an implementation-specific JSON parser on JSONtext.
        val c = objectMapper.readValue<CollectedClientData>(jsonText)

        // step 12
        // Verify that the value of C.type is the string webauthn.get.
        if (c.type != "webauthn.get") {
            throw Exception("invalid type: ${c.type}")
        }

        // step 13
        // Verify that the value of C.challenge equals the base64url encoding of options.challenge.
        val challenge = authenticationChallengeRepository.find(
            UUID.fromString(sessionId)
        ) ?: throw Exception("challenge was not found")
        if (Base64.getDecoder().decode(c.challenge).toString(Charsets.UTF_8) != challenge.challenge) {
            throw Exception("invalid challenge of CollectedClientData")
        }

        // step 14
        // Verify that the value of C.origin is an origin expected by the Relying Party.
        if (c.origin != ServerConfig.origin) {
            throw Exception("invalid origin of CollectedClientData")
        }

        // step 15
        // If C.topOrigin is present:
        //  1. Verify that the Relying Party expects this credential to be used within an iframe that is not same-origin with its ancestors.
        //  2. Verify that the value of C.topOrigin matches the origin of a page that the Relying Party expects to be sub-framed within.
        if (c.topOrigin !== null) {
            // この credential が別 origin の iframe 内で作られて良いことを確認する
            if (c.topOrigin !== "expected value") {
                throw Exception("invalid top Origin: ${c.topOrigin}")
            }
        }

        // step 16
        // Verify that the rpIdHash in authData is the SHA-256 hash of the RP ID expected by the Relying Party.
        if (!authData.rpidHash.contentEquals(
                MessageDigest.getInstance("SHA-256")
                    .digest(ServerConfig.rpid.toByteArray())
            )
        ) {
            throw Exception("invalid rpidHash of authenticatorData")
        }

        // step 17
        // Verify that the UP bit of the flags in authData is set.
        if (!authData.flags.up) {
            throw Exception("UP bit of the flags in authenticatorData is not set")
        }

        // step 18
        // Determine whether user verification is required for this assertion.
        // User verification SHOULD be required if, and only if, options.userVerification is set to required.
        if (ServerConfig.userVerificationRequirement == UserVerificationRequirement.Required &&
            !authData.flags.uv
        ) {
            throw Exception("userVerification is required")
        }

        // step 19
        // If the BE bit of the flags in authData is not set, verify that the BS bit is not set.
        if (!authData.flags.be && authData.flags.bs) {
            throw Exception("BS is set although BE is not set.")
        }

        // step 20: skip
        // If the credential backup state is used as part of Relying Party business logic or policy,
        // let currentBe and currentBs be the values of the BE and BS bits, respectively, of the flags in authData.
        // Compare currentBe and currentBs with credentialRecord.backupEligible and credentialRecord.backupState:
        //  1. If credentialRecord.backupEligible is set, verify that currentBe is set.
        //  2. If credentialRecord.backupEligible is not set, verify that currentBe is not set.
        //  3. Apply Relying Party policy, if any.

        // step 21: skip
        // Verify that the values of the client extension outputs in clientExtensionResults and the authenticator extension outputs in the extensions in authData are as expected,
        // considering the client extension input values that were given in options.extensions and any specific policy of the Relying Party regarding unsolicited extensions,
        // i.e., those that were not specified as part of options.extensions. In the general case, the meaning of "are as expected" is specific to the Relying Party and which extensions are in use.

        // step 22: skip
        // Let hash be the result of computing a hash over the cData using SHA-256.
        val hash = MessageDigest.getInstance("SHA-256").digest(cData)

        // TODO: step 23
        // Using credentialRecord.publicKey, verify that sig is a valid signature over the binary concatenation of authData and hash.
        val credentialRecord = userAuthenticatorNewRepository.find(
            Base64.getDecoder().decode(request.id)
        ) ?: throw Exception("credential not found")

        // step 24
        // If authData.signCount is nonzero or credentialRecord.signCount is nonzero, then run the following sub-step:
        // If authData.signCount is
        //   greater than credentialRecord.signCount:
        //     The signature counter is valid.
        //   less than or equal to credentialRecord.signCount:
        //     This is a signal that the authenticator may be cloned, i.e. at least two copies of the credential private key may exist and are being used in parallel.
        //     Relying Parties should incorporate this information into their risk scoring.
        //     Whether the Relying Party updates credentialRecord.signCount below in this case, or not, or fails the authentication ceremony or not, is Relying Party-specific.
        if (
            authData.signCount != 0L &&
            credentialRecord.signCount != 0L &&
            authData.signCount <= credentialRecord.signCount
        ) {
            throw Exception("invalid signCount. authData.signCount: ${authData.signCount}, credential.signCount: ${credentialRecord.signCount}")
        }

        // step 25: skip
        // If response.attestationObject is present and the Relying Party wishes to verify the attestation
        // then perform CBOR decoding on attestationObject to obtain the attestation statement format fmt, and the attestation statement attStmt.

        // step 26 (skip 3, 4)
        // Update credentialRecord with new state values:
        //  1. Update credentialRecord.signCount to the value of authData.signCount.
        //  2. Update credentialRecord.backupState to the value of currentBs.
        //  3. If credentialRecord.uvInitialized is false, update it to the value of the UV bit in the flags in authData.
        //     This change SHOULD require authorization by an additional authentication factor equivalent to WebAuthn user verification; if not authorized, skip this step.
        //  4. OPTIONALLY, if response.attestationObject is present, update credentialRecord.attestationObject to the value of response.attestationObject
        //     and update credentialRecord.attestationClientDataJSON to the value of response.clientDataJSON.
        val newCredential = credentialRecord.update(
            signCount = authData.signCount,
            currentBs = authData.flags.bs,
        )
        userAuthenticatorNewRepository.save(newCredential)

        // step 27
        // If all the above steps are successful, continue with the authentication ceremony as appropriate.
        // Otherwise, fail the authentication ceremony.

        val session = LoginSession(
            id = UUID.randomUUID().toString(),
            userId = user.id
        )
        loginSessionRepository.add(session)

        return session
    }


    @Suppress("unused")
    fun handleWithWebauthn4j(
        request: AuthenticationRequest,
        sessionId: String,
    ): LoginSession {
        val authenticationRequest = com.webauthn4j.data.AuthenticationRequest(
            Base64Util.decode(request.id),
            Base64Util.decode(request.response.userHandle),
            Base64Util.decode(request.response.authenticatorData),
            Base64Util.decode(request.response.clientDataJSON),
            Base64Util.decode(request.response.signature)
        )

        val serverChallenge = authenticationChallengeRepository.find(
            UUID.fromString(sessionId)
        ) ?: throw Exception("challenge was not found")

        if (serverChallenge.expiresAt <= Instant.now()) {
            throw Exception("timeout")
        }

        val userAuthenticator = userAuthenticatorRepository.find(authenticationRequest.credentialId)
            ?: throw Exception("authenticator が登録されていません")

        val authenticationParameters = AuthenticationParameters(
            ServerProperty(
                Origin.create(ServerConfig.origin),
                ServerConfig.rpid,
                DefaultChallenge(serverChallenge.challenge.toByteArray()),
                null
            ),
            userAuthenticator.authenticator,
            listOf(userAuthenticator.authenticator.attestedCredentialData.credentialId),
            true,
            true,
        )

        val manager = WebAuthnAuthenticationManager()
        try {
            manager.validate(authenticationRequest, authenticationParameters)
        } catch (e: ValidationException) {
            throw Exception(e)
        }

        userAuthenticator.authenticator.counter++
        userAuthenticatorRepository.update(userAuthenticator)

        val session = LoginSession(
            id = UUID.randomUUID().toString(),
            userId = userAuthenticator.userId
        )
        loginSessionRepository.add(session)

        return session
    }
}
