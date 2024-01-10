package me.yapoo.webauthn.config

import me.yapoo.webauthn.dto.AttestationConveyancePreference
import me.yapoo.webauthn.dto.COSEAlgorithmIdentifier
import me.yapoo.webauthn.dto.UserVerificationRequirement

object ServerConfig {

    const val origin: String = "http://localhost:3000"

    const val rpid: String = "localhost"

    const val name: String = "Sample FIDO2 authentication server"

    val userVerificationRequirement = UserVerificationRequirement.Preferred

    val allowedCoseAlgorithms = listOf(
        COSEAlgorithmIdentifier.ES256
    )

    val attestation = AttestationConveyancePreference.None
}
