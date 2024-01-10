package me.yapoo.webauthn.dto

data class CollectedClientData(
    val type: String,
    val challenge: String,
    val origin: String,
    val topOrigin: String?,
    val crossOrigin: Boolean?,
    val tokenBinding: TokenBinding? = null,
)
