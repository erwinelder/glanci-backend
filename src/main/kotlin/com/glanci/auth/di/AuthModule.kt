package com.glanci.auth.di

import com.glanci.auth.data.repository.UserRepository
import com.glanci.auth.data.repository.UserRepositoryImpl
import com.glanci.auth.domain.service.AuthServiceImpl
import com.glanci.auth.domain.service.FirebaseAuthService
import com.glanci.auth.domain.service.getFirebaseAuthService
import com.glanci.auth.shared.service.AuthService
import org.koin.dsl.module

val authModule = module {

    /* ------------ Repositories ------------ */

    single<UserRepository> {
        UserRepositoryImpl(databaseProvider = get())
    }

    /* ------------ Services ------------ */

    single<FirebaseAuthService> {
        getFirebaseAuthService()
    }

    single<AuthService> {
        AuthServiceImpl(
            firebaseAuthService = get(),
            userRepository = get()
        )
    }

}