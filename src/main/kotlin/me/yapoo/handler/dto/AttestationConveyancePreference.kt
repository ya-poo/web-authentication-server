package me.yapoo.handler.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class AttestationConveyancePreference(
    @JsonValue
    val value: String,
) {
    None("none"),
    Indirect("indirect"),
    Direct("direct"),
    Enterprise("enterprise")
}
