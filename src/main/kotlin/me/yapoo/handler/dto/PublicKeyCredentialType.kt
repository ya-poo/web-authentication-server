package me.yapoo.handler.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class PublicKeyCredentialType(
    @JsonValue
    val value: String,
) {
    PublicKey("public-key")
}
