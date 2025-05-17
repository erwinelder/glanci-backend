package com.glanci.auth.domain.service

import com.glanci.auth.domain.model.firebase.FirebaseUser

interface FirebaseAuthService {

    suspend fun signIn(email: String, password: String): FirebaseUser

    suspend fun signUp(email: String, password: String): FirebaseUser

    suspend fun sendEmailVerification(idToken: String)

    suspend fun verifyEmail(oobCode: String): String


    suspend fun requestEmailUpdate(idToken: String, newEmail: String)

    suspend fun verifyEmailUpdate(oobCode: String): String


    suspend fun updatePassword(idToken: String, newPassword: String)

    suspend fun requestPasswordReset(email: String)

    suspend fun verifyPasswordReset(oobCode: String, newPassword: String)


    suspend fun deleteUser(email: String, password: String)

}