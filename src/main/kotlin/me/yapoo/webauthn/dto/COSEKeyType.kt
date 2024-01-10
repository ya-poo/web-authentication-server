package me.yapoo.webauthn.dto

// https://www.iana.org/assignments/cose/cose.xhtml#key-type
enum class COSEKeyType(
    val value: Int
) {
    EC2(2),

    @Deprecated("unsupported")
    Reserved(0),

    @Deprecated("unsupported")
    OKP(1),

    @Deprecated("unsupported")
    RSA(3),

    @Deprecated("unsupported")
    Symmetric(4),
}
