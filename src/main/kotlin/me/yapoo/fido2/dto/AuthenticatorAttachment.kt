package me.yapoo.fido2.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class AuthenticatorAttachment(
    @JsonValue
    val value: String,
) {
    Platform("platform"),
    CrossPlatform("cross-platform")
}
