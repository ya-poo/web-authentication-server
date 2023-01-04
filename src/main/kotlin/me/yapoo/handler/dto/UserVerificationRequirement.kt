package me.yapoo.handler.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class UserVerificationRequirement(
    @JsonValue
    val value: String,
) {
    Required("required"),
    Preferred("preferred"),
    Discouraged("discouraged"),
}
