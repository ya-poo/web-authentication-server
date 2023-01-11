package me.yapoo.fido2.handler.authentication

data class AuthenticationRequest(
    val id: String,
    val rawId: String,
    val response: AuthenticatorAssertionResponse
) {

    data class AuthenticatorAssertionResponse(
        val authenticatorData: String,
        val signature: String,
        val userHandle: String,
        val clientDataJSON: String,
    )
}
