package com.glanci.auth.domain.service

import com.glanci.auth.domain.model.firebase.FirebaseUser
import com.glanci.request.domain.error.AuthDataError
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult

interface FirebaseAuthService {

    suspend fun signIn(email: String, password: String): ResultData<FirebaseUser, AuthDataError>

    suspend fun signUp(email: String, password: String): ResultData<FirebaseUser, AuthDataError>

    suspend fun sendEmailVerification(idToken: String): SimpleResult<AuthDataError>

    suspend fun verifyEmail(oobCode: String): ResultData<String, AuthDataError>


    suspend fun requestEmailUpdate(idToken: String, newEmail: String): SimpleResult<AuthDataError>

    suspend fun verifyEmailUpdate(oobCode: String): ResultData<String, AuthDataError>


    suspend fun updatePassword(idToken: String, newPassword: String): SimpleResult<AuthDataError>

    suspend fun requestPasswordReset(email: String): SimpleResult<AuthDataError>

    suspend fun verifyPasswordReset(oobCode: String, newPassword: String): SimpleResult<AuthDataError>


    suspend fun deleteUser(email: String, password: String): SimpleResult<AuthDataError>

}