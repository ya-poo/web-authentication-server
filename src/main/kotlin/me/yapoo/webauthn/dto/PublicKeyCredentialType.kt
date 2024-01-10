package me.yapoo.webauthn.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class PublicKeyCredentialType(
    @JsonValue
    val value: String,
) {
    PublicKey("public-key")
}
