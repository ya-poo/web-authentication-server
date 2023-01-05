package me.yapoo.fido2.handler.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class PublicKeyCredentialType(
    @JsonValue
    val value: String,
) {
    PublicKey("public-key")
}
