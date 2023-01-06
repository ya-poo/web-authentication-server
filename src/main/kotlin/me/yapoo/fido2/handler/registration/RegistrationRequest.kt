package me.yapoo.fido2.handler.registration

/*
 * ブラウザで取得した AuthenticatorAttestationResponse の同名のプロパティ (ArrayBuffer) を
 * Base64 エンコードしてサーバー側に送信する。
 */
data class RegistrationRequest(
    val clientDataJSON: String,
    val attestationObject: String,
)
