package me.yapoo.fido2.di

import me.yapoo.fido2.domain.authentication.AuthenticationChallengeRepository
import me.yapoo.fido2.domain.authentication.UserAuthenticatorNewRepository
import me.yapoo.fido2.domain.authentication.UserAuthenticatorRepository
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
    single { UserAuthenticatorNewRepository() }
    single { PreregistrationHandler(get(), get()) }
    single { UserAuthenticatorRepository() }
    single { RegistrationHandler(get(), get(), get(), get()) }
    single { AuthenticationChallengeRepository() }
    single { PreAuthenticationHandler(get()) }
    single { LoginSessionRepository() }
    single { AuthenticationHandler(get(), get(), get()) }
}
