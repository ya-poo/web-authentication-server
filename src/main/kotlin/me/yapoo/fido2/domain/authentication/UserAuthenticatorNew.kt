package me.yapoo.fido2.domain.authentication

import com.upokecenter.cbor.CBORObject
import me.yapoo.fido2.dto.AttestationObject
import me.yapoo.fido2.dto.CollectedClientData

class UserAuthenticatorNew(
    val type: String,
    val id: ByteArray,
    val publicKey: CBORObject,
    val signCount: Long,
    val uvInitialised: Boolean,
    val transports: List<String>,
    val backupEligible: Boolean,
    val backupState: Boolean,
    val attestationObject: AttestationObject,
    val attestationClientDataJson: CollectedClientData,
    val userId: String,
)
