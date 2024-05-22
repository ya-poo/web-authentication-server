package me.yapoo.webauthn.domain.authentication

import me.yapoo.webauthn.dto.AttestationObject
import me.yapoo.webauthn.dto.CollectedClientData
import me.yapoo.webauthn.dto.CredentialPublicKey

class UserCredential(
    val type: String,
    val id: ByteArray,
    val publicKey: CredentialPublicKey,
    val signCount: Long,
    val uvInitialised: Boolean,
    val transports: List<String>,
    val backupEligible: Boolean,
    val backupState: Boolean,
    val attestationObject: AttestationObject,
    val attestationClientDataJson: CollectedClientData,
    val userId: String,
) {

    fun update(
        signCount: Long,
        currentBs: Boolean,
    ): UserCredential {
        return UserCredential(
            type = type,
            id = id,
            publicKey = publicKey,
            signCount = signCount,
            uvInitialised = uvInitialised,
            transports = transports,
            backupEligible = backupEligible,
            backupState = currentBs,
            attestationObject = attestationObject,
            attestationClientDataJson = attestationClientDataJson,
            userId = userId,
        )
    }
}
