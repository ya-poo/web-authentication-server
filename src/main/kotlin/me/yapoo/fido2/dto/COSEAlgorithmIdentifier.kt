package me.yapoo.fido2.dto

import com.fasterxml.jackson.annotation.JsonValue

/*
 * COSE Algorithms
 * https://www.iana.org/assignments/cose/cose.xhtml#algorithms
 */
enum class COSEAlgorithmIdentifier(
    @JsonValue
    val value: Long
) {
    ES256(-7),
    ES384(-35),
    ES512(-36),
    EdDSA(-8),
}
