package com.glanci.auth.di

import com.glanci.auth.data.repository.UserRepository
import com.glanci.auth.data.repository.UserRepositoryImpl
import com.glanci.auth.domain.service.FirebaseAuthService
import com.glanci.auth.domain.service.UserService
import com.glanci.auth.domain.service.getFirebaseAuthService
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

    single {
        UserService(userRepository = get())
    }

}