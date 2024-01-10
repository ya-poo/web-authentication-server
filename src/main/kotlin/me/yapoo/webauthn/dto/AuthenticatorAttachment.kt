package me.yapoo.webauthn.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class AuthenticatorAttachment(
    @JsonValue
    val value: String,
) {
    Platform("platform"),
    CrossPlatform("cross-platform")
}
