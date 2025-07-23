package com.glanci.auth.domain.service

import com.glanci.auth.domain.model.firebase.FirebaseUser
import com.glanci.request.domain.error.AuthError
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult

class FirebaseAuthServiceDevelopment : FirebaseAuthService {

    override suspend fun signIn(email: String, password: String): ResultData<FirebaseUser, AuthError> {
        val user = FirebaseUser(
            idToken = "token",
            uid = "uid",
            email = email,
            emailVerified = true
        )

        return ResultData.Success(data = user)
    }

    override suspend fun signUp(email: String, password: String): ResultData<FirebaseUser, AuthError> {
        val user = FirebaseUser(
            idToken = "token",
            uid = "uid",
            email = email,
            emailVerified = true
        )

        return ResultData.Success(data = user)
    }

    override suspend fun sendEmailVerification(idToken: String): SimpleResult<AuthError> {
        return SimpleResult.Success()
    }

    override suspend fun verifyEmail(oobCode: String): ResultData<String, AuthError> {
        return ResultData.Success(data = "email")
    }


    override suspend fun requestEmailUpdate(idToken: String, newEmail: String): SimpleResult<AuthError> {
        return SimpleResult.Success()
    }

    override suspend fun verifyEmailUpdate(oobCode: String): ResultData<String, AuthError> {
        return ResultData.Success(data = "email")
    }


    override suspend fun updatePassword(idToken: String, newPassword: String): SimpleResult<AuthError> {
        return SimpleResult.Success()
    }

    override suspend fun requestPasswordReset(email: String): SimpleResult<AuthError> {
        return SimpleResult.Success()
    }

    override suspend fun verifyPasswordReset(oobCode: String, newPassword: String): SimpleResult<AuthError> {
        return SimpleResult.Success()
    }


    override suspend fun deleteUser(email: String, password: String): SimpleResult<AuthError> {
        return SimpleResult.Success()
    }

}