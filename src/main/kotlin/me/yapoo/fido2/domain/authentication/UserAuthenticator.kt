package me.yapoo.fido2.domain.authentication

import me.yapoo.fido2.dto.AttestationObject
import me.yapoo.fido2.dto.CollectedClientData
import me.yapoo.fido2.dto.CredentialPublicKey

class UserAuthenticator(
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
    ): UserAuthenticator {
        return UserAuthenticator(
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
