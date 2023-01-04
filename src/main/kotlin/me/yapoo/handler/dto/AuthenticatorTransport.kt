package me.yapoo.handler.dto

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
