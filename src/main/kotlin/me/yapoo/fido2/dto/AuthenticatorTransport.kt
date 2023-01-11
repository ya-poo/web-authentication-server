package me.yapoo.fido2.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class AuthenticatorTransport(
    @JsonValue
    val value: String
) {
    Usb("usb"),
    Nfc("nfc"),
    Ble("ble"),
    Internal("internal"),
}
