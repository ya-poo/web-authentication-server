package me.yapoo.webauthn.di

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.yapoo.webauthn.domain.authentication.AuthenticationChallengeRepository
import me.yapoo.webauthn.domain.authentication.UserAuthenticatorRepository
import me.yapoo.webauthn.domain.authentication.UserWebAuthn4jAuthenticatorRepository
import me.yapoo.webauthn.domain.registration.UserRegistrationChallengeRepository
import me.yapoo.webauthn.domain.session.LoginSessionRepository
import me.yapoo.webauthn.domain.user.UserRepository
import me.yapoo.webauthn.handler.authentication.AuthenticationHandler
import me.yapoo.webauthn.handler.preauthentication.PreAuthenticationHandler
import me.yapoo.webauthn.handler.preregistration.PreregistrationHandler
import me.yapoo.webauthn.handler.registration.RegistrationHandler
import org.koin.dsl.module

val appModule = module {
    single { UserRepository() }
    single { UserRegistrationChallengeRepository() }
    single { UserAuthenticatorRepository() }
    single {
        jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    single { PreregistrationHandler(get(), get()) }
    single { UserWebAuthn4jAuthenticatorRepository() }
    single { RegistrationHandler(get(), get(), get(), get(), get()) }
    single { AuthenticationChallengeRepository() }
    single { PreAuthenticationHandler(get()) }
    single { LoginSessionRepository() }
    single { AuthenticationHandler(get(), get(), get(), get(), get(), get()) }
}
