package me.yapoo.webauthn.handler.registration

/*
 * ブラウザで ArrayBuffer として取得するものは Base64 エンコードしてサーバー側に送信する。
 */
data class RegistrationRequest(
    val type: String,
    val id: String,
    val rawId: String,
    val clientDataJSON: String,
    val attestationObject: String,
    val transports: List<String>,
)
