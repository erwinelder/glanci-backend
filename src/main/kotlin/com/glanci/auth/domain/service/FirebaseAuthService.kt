package com.glanci.auth.domain.service

import com.glanci.auth.domain.model.firebase.FirebaseUser
import com.glanci.request.domain.error.AuthError
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult

interface FirebaseAuthService {

    suspend fun signIn(email: String, password: String): ResultData<FirebaseUser, AuthError>

    suspend fun signUp(email: String, password: String): ResultData<FirebaseUser, AuthError>

    suspend fun sendEmailVerification(idToken: String): SimpleResult<AuthError>

    suspend fun verifyEmail(oobCode: String): ResultData<String, AuthError>


    suspend fun requestEmailUpdate(idToken: String, newEmail: String): SimpleResult<AuthError>

    suspend fun verifyEmailUpdate(oobCode: String): ResultData<String, AuthError>


    suspend fun updatePassword(idToken: String, newPassword: String): SimpleResult<AuthError>

    suspend fun requestPasswordReset(email: String): SimpleResult<AuthError>

    suspend fun verifyPasswordReset(oobCode: String, newPassword: String): SimpleResult<AuthError>


    suspend fun deleteUser(email: String, password: String): SimpleResult<AuthError>

}