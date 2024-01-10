package me.yapoo.webauthn.dto

data class TokenBinding(
    val status: TokenBindingStatus,
    val id: String?,
)
