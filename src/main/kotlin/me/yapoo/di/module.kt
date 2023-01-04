package me.yapoo.di

import me.yapoo.domain.registration.UserRegistrationChallengeRepository
import me.yapoo.domain.user.UserRepository
import me.yapoo.handler.authentication.PreregistrationHandler
import org.koin.dsl.module

val appModule = module {
    single { UserRepository() }
    single { UserRegistrationChallengeRepository() }
    single { PreregistrationHandler(get(), get()) }
}
