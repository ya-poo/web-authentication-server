package me.yapoo.fido2.handler.registration

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.upokecenter.cbor.CBORObject
import com.webauthn4j.WebAuthnRegistrationManager
import com.webauthn4j.authenticator.AuthenticatorImpl
import com.webauthn4j.data.PublicKeyCredentialParameters
import com.webauthn4j.data.PublicKeyCredentialType
import com.webauthn4j.data.RegistrationParameters
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier
import com.webauthn4j.data.client.Origin
import com.webauthn4j.data.client.challenge.DefaultChallenge
import com.webauthn4j.server.ServerProperty
import com.webauthn4j.util.Base64Util
import com.webauthn4j.validator.exception.ValidationException
import me.yapoo.fido2.config.ServerConfig
import me.yapoo.fido2.domain.authentication.UserAuthenticator
import me.yapoo.fido2.domain.authentication.UserAuthenticatorNew
import me.yapoo.fido2.domain.authentication.UserAuthenticatorNewRepository
import me.yapoo.fido2.domain.authentication.UserAuthenticatorRepository
import me.yapoo.fido2.domain.registration.UserRegistrationChallengeRepository
import me.yapoo.fido2.domain.user.User
import me.yapoo.fido2.domain.user.UserRepository
import me.yapoo.fido2.dto.CollectedClientData
import me.yapoo.fido2.dto.RawAttestationObject
import me.yapoo.fido2.dto.UserVerificationRequirement
import me.yapoo.fido2.dto.toAttestationObject
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64
import java.util.UUID

class RegistrationHandler(
    private val userRegistrationChallengeRepository: UserRegistrationChallengeRepository,
    private val userAuthenticatorRepository: UserAuthenticatorRepository,
    private val userAuthenticatorNewRepository: UserAuthenticatorNewRepository,
    private val userRepository: UserRepository,
) {

    private val objectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun handle(
        request: RegistrationRequest,
        sessionId: String,
    ) {
        handle2(request, sessionId)

        val manager = WebAuthnRegistrationManager.createNonStrictWebAuthnRegistrationManager()

        val registrationRequest = com.webauthn4j.data.RegistrationRequest(
            Base64Util.decode(request.attestationObject),
            Base64Util.decode(request.clientDataJSON)
        )
        val registrationData = manager.parse(registrationRequest)
        if (registrationData.collectedClientData == null) {
            throw Exception()
        }

        val serverChallenge = userRegistrationChallengeRepository.find(
            UUID.fromString(sessionId)
        ) ?: throw Exception("registration session に紐づくチャレンジがありません")

        if (serverChallenge.expiresAt <= Instant.now()) {
            throw Exception("timeout")
        }

        val registrationParameters = RegistrationParameters(
            ServerProperty(
                Origin.create(ServerConfig.origin),
                ServerConfig.rpid,
                DefaultChallenge(serverChallenge.challenge.toByteArray()),
                null
            ),
            listOf(
                PublicKeyCredentialParameters(
                    PublicKeyCredentialType.PUBLIC_KEY,
                    COSEAlgorithmIdentifier.ES256
                )
            ),
            false,
            true
        )

        try {
            manager.validate(registrationData, registrationParameters)
        } catch (e: ValidationException) {
            throw Exception(e)
        }

        val authenticator = AuthenticatorImpl(
            registrationData.attestationObject!!.authenticatorData.attestedCredentialData!!,
            registrationData.attestationObject?.attestationStatement,
            registrationData.attestationObject!!.authenticatorData.signCount
        )

        userAuthenticatorRepository.add(
            UserAuthenticator(
                userId = serverChallenge.userId,
                authenticator = authenticator
            )
        )

        userRepository.add(
            User(
                username = serverChallenge.username,
                displayName = serverChallenge.username,
                id = serverChallenge.userId
            )
        )
    }

    fun handle2(
        request: RegistrationRequest,
        sessionId: String,
    ) {
        // step 5
        // Let JSONtext be the result of running UTF-8 decode on the value of response.clientDataJSON.
        val jsonText = Base64.getDecoder().decode(request.clientDataJSON)

        // step 6
        // Let C, the client data claimed as collected during the credential creation,
        // be the result of running an implementation-specific JSON parser on JSONtext.
        val c = objectMapper.readValue<CollectedClientData>(jsonText)

        // step 7
        // Verify that the value of C.type is webauthn.create.
        if (c.type != "webauthn.create") {
            throw Exception("invalid type of CollectedClientData: ${c.type}")
        }

        // step 8
        // Verify that the value of C.challenge equals the base64url encoding of options.challenge.
        val challenge = userRegistrationChallengeRepository.find(UUID.fromString(sessionId))
            ?: throw Exception("invalid challenge of CollectedClientData")

        // step 9
        // Verify that the value of C.origin is an origin expected by the Relying Party.
        if (c.origin != ServerConfig.origin) {
            throw Exception("invalid origin of CollectedClientData")
        }

        // step 10
        // If C.topOrigin is present:
        //  1. Verify that the Relying Party expects that this credential would have been created within an iframe that is not same-origin with its ancestors.
        //  2. Verify that the value of C.topOrigin matches the origin of a page that the Relying Party expects to be sub-framed within.
        if (c.topOrigin !== null) {
            // この credential が別 origin の iframe 内で作られて良いことを確認する
            if (c.topOrigin !== "expected value") {
                throw Exception("invalid top Origin: ${c.topOrigin}")
            }
        }

        // step 11: skip (attestation = none では使わないため)
        // Let hash be the result of computing a hash over response.clientDataJSON using SHA-256.

        // step 12
        // Perform CBOR decoding on the attestationObject field of the AuthenticatorAttestationResponse structure
        // to obtain the attestation statement format fmt, the authenticator data authData, and the attestation statement attStmt.
        val attestationObject = CBORObject.DecodeFromBytes(Base64.getDecoder().decode(request.attestationObject))
            .let {
                RawAttestationObject(
                    fmt = it["fmt"].AsString(),
                    attStmt = it["attStmt"].ToObject(Map::class.java),
                    authData = it["authData"].ToObject(ByteArray::class.java)
                )
            }
            .toAttestationObject()

        // step 13
        // Verify that the rpIdHash in authData is the SHA-256 hash of the RP ID expected by the Relying Party.
        if (!attestationObject.authenticatorData.rpidHash.contentEquals(
                MessageDigest.getInstance("SHA-256")
                    .digest(ServerConfig.rpid.toByteArray())
            )
        ) {
            throw Exception("invalid rpidHash of AttestationObject")
        }

        // step 14
        // Verify that the UP bit of the flags in authData is set.
        if (!attestationObject.authenticatorData.flags.up) {
            throw Exception("invalid User Present (UP) value: false")
        }

        // step 15
        // If the Relying Party requires user verification for this registration, verify that the UV bit of the flags in authData is set.
        if (ServerConfig.userVerificationRequirement == UserVerificationRequirement.Required && !attestationObject.authenticatorData.flags.uv) {
            throw Exception("invalid User Verification (UV) flag: false (UserVerificationRequirement: ${ServerConfig.userVerificationRequirement})")
        }

        // step 16
        // If the BE bit of the flags in authData is not set, verify that the BS bit is not set.
        if (!attestationObject.authenticatorData.flags.be && attestationObject.authenticatorData.flags.bs) {
            throw Exception("BS is set although BE is not set.")
        }

        // step 17: skip
        // If the Relying Party uses the credential’s backup eligibility to inform its user experience flows and/or policies,
        // evaluate the BE bit of the flags in authData.

        // step 18: skip
        // If the Relying Party uses the credential’s backup state to inform its user experience flows and/or policies,
        // evaluate the BS bit of the flags in authData.

        // step 19
        // Verify that the "alg" parameter in the credential public key in authData matches the alg attribute of one of the items in options.pubKeyCredParams.
        val alg = attestationObject.authenticatorData.attestedCredentialData.credentialPublicKey[3].AsInt32()
        if (alg !in ServerConfig.allowedCoseAlgorithms.map { it.value }) {
            throw Exception("invalid alg parameter of credentialPublicKey: $alg")
        }

        // step 20: skip
        // Verify that the values of the client extension outputs in clientExtensionResults and the authenticator extension outputs in the extensions in authData are as expected,
        // considering the client extension input values that were given in options.extensions and any specific policy of the Relying Party regarding unsolicited extensions,
        // i.e., those that were not specified as part of options.extensions. In the general case, the meaning of "are as expected" is specific to the Relying Party and which extensions are in use.

        // step 21
        // Determine the attestation statement format by performing a USASCII case-sensitive match
        // on fmt against the set of supported WebAuthn Attestation Statement Format Identifier values.
        if (attestationObject.fmt != ServerConfig.attestation.value) {
            throw Exception("invalid attestation format: ${attestationObject.fmt}, expected: ${ServerConfig.attestation.value}")
        }

        // step 22
        // Verify that attStmt is a correct attestation statement, conveying a valid attestation signature,
        // by using the attestation statement format fmt’s verification procedure given attStmt, authData and hash.
        if (attestationObject.attStmt.isNotEmpty()) {
            // Attestation は None のみのサポートなので、 attStmt が空であることを確認しておく
            throw Exception("invalid attStmt: ${attestationObject.attStmt}, expected = empty")
        }

        // step 23: skip
        // If validation is successful, obtain a list of acceptable trust anchors (i.e. attestation root certificates)
        // for that attestation type and attestation statement format fmt, from a trusted source or from policy.

        // step 24: skip
        // Assess the attestation trustworthiness using the outputs of the verification procedure in step 21

        // step 25
        // Verify that the credentialId is ≤ 1023 bytes. Credential IDs larger than this many bytes SHOULD cause the RP to fail this registration ceremony
        if (attestationObject.authenticatorData.attestedCredentialData.credentialId.size > 1023) {
            throw Exception("invalid credentialId length: ${attestationObject.authenticatorData.attestedCredentialData.credentialId.size}")
        }

        // step 26
        // Verify that the credentialId is not yet registered for any user.
        if (userAuthenticatorNewRepository.find(attestationObject.authenticatorData.attestedCredentialData.credentialId) != null) {
            throw Exception("already registered credential")
        }

        // step 27
        // If the attestation statement attStmt verified successfully and is found to be trustworthy,
        // then create and store a new credential record in the user account that was denoted in options.user
        userAuthenticatorNewRepository.add(
            UserAuthenticatorNew(
                type = request.type,
                id = attestationObject.authenticatorData.attestedCredentialData.credentialId,
                publicKey = attestationObject.authenticatorData.attestedCredentialData.credentialPublicKey,
                signCount = attestationObject.authenticatorData.signCount,
                uvInitialised = attestationObject.authenticatorData.flags.uv,
                transports = request.transports,
                backupEligible = attestationObject.authenticatorData.flags.be,
                backupState = attestationObject.authenticatorData.flags.bs,
                attestationObject = attestationObject,
                attestationClientDataJson = c,
                userId = challenge.userId,
            )
        )

        // step 28: skip
        // If the attestation statement attStmt successfully verified but is not trustworthy per step 23 above,
        // the Relying Party SHOULD fail the registration ceremony.
    }
}
