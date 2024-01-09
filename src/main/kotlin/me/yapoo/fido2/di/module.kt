package me.yapoo.fido2.di

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.yapoo.fido2.domain.authentication.AuthenticationChallengeRepository
import me.yapoo.fido2.domain.authentication.UserAuthenticatorRepository
import me.yapoo.fido2.domain.authentication.UserWebAuthn4jAuthenticatorRepository
import me.yapoo.fido2.domain.registration.UserRegistrationChallengeRepository
import me.yapoo.fido2.domain.session.LoginSessionRepository
import me.yapoo.fido2.domain.user.UserRepository
import me.yapoo.fido2.handler.authentication.AuthenticationHandler
import me.yapoo.fido2.handler.preauthentication.PreAuthenticationHandler
import me.yapoo.fido2.handler.preregistration.PreregistrationHandler
import me.yapoo.fido2.handler.registration.RegistrationHandler
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
