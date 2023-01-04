package me.yapoo.handler.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class ResidentKeyRequirement(
    @JsonValue
    val value: String,
) {
    Discouraged("discouraged"),
    Preferred("preferred"),
    Required("required"),
}
