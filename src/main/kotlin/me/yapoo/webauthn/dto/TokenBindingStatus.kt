package me.yapoo.webauthn.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class TokenBindingStatus(
    @JsonValue
    val value: String
) {
    Present("present"),
    Supported("supported")
}
