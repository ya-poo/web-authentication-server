package me.yapoo.fido2.di

import me.yapoo.fido2.domain.authentication.AuthenticatorRepository
import me.yapoo.fido2.domain.authentication.UserAuthenticationChallengeRepository
import me.yapoo.fido2.domain.registration.UserRegistrationChallengeRepository
import me.yapoo.fido2.domain.user.UserRepository
import me.yapoo.fido2.handler.authentication.AuthenticationHandler
import me.yapoo.fido2.handler.preauthentication.PreAuthenticationHandler
import me.yapoo.fido2.handler.preregistration.PreregistrationHandler
import me.yapoo.fido2.handler.registration.RegistrationHandler
import org.koin.dsl.module

val appModule = module {
    single { UserRepository() }
    single { UserRegistrationChallengeRepository() }
    single { PreregistrationHandler(get(), get()) }
    single { AuthenticatorRepository() }
    single { RegistrationHandler(get(), get(), get()) }
    single { UserAuthenticationChallengeRepository() }
    single { PreAuthenticationHandler(get(), get()) }
    single { AuthenticationHandler(get(), get()) }
}
