package me.yapoo.fido2.dto

import com.fasterxml.jackson.annotation.JsonValue

data class CollectedClientData(
    val type: String,
    val challenge: String,
    val origin: String,
    val topOrigin: String?,
    val crossOrigin: Boolean?,

    // [RESERVED]
    val tokenBinding: TokenBinding?,
)

data class TokenBinding(
    val status: String,
    val id: String?,
)

enum class TokenBindingStatus(
    @JsonValue
    val value: String
) {
    Present("present"),
    Supported("supported")
}
