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

    override suspend fun verifyEmail(oobCode: String): String {
        return "email"
    }


    override suspend fun requestEmailUpdate(idToken: String, newEmail: String) {}

    override suspend fun verifyEmailUpdate(oobCode: String): String {
        return "email"
    }


    override suspend fun updatePassword(idToken: String, newPassword: String) {}

    override suspend fun requestPasswordReset(email: String) {}

    override suspend fun verifyPasswordReset(oobCode: String, newPassword: String) {}


    override suspend fun deleteUser(email: String, password: String) {}

}