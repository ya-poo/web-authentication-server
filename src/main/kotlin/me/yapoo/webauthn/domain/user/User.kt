package me.yapoo.webauthn.domain.user

data class User(
    val username: String, // email アドレスを想定
    val displayName: String,
    val id: String,
)
