package com.glanci.auth.domain.service

import com.glanci.auth.domain.model.firebase.FirebaseUser

class FirebaseAuthServiceDevelopment : FirebaseAuthService {

    override suspend fun signIn(email: String, password: String): FirebaseUser {
        return FirebaseUser(
            idToken = "token",
            uid = "uid",
            email = email,
            emailVerified = true
        )
    }

    override suspend fun signUp(email: String, password: String): FirebaseUser {
        return FirebaseUser(
            idToken = "token",
            uid = "uid",
            email = email,
            emailVerified = true
        )
    }

    override suspend fun sendEmailVerification(idToken: String) {}

    override suspend fun deleteUser(email: String, password: String) {}

}