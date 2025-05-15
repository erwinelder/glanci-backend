package com.glanci.auth.domain.service

fun getFirebaseAuthService(): FirebaseAuthService {
    val environment = System.getenv("ENVIRONMENT")

    return if (environment == "production") {
        FirebaseAuthServiceProduction()
    } else {
        FirebaseAuthServiceDevelopment()
    }
}