package me.yapoo.fido2.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class UserVerificationRequirement(
    @JsonValue
    val value: String,
) {
    Required("required"),
    Preferred("preferred"),
    Discouraged("discouraged"),
}
