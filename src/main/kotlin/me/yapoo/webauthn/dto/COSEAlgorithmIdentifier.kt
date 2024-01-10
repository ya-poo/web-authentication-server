package me.yapoo.webauthn.dto

import com.fasterxml.jackson.annotation.JsonValue

/*
 * COSE Algorithms
 * https://www.iana.org/assignments/cose/cose.xhtml#algorithms
 */
enum class COSEAlgorithmIdentifier(
    @JsonValue
    val value: Int
) {
    ES256(-7),
    ES384(-35),
    ES512(-36),
    EdDSA(-8),
}
