package com.glanci.auth.domain.service

import com.glanci.auth.domain.model.firebase.FirebaseUser
import com.glanci.request.domain.error.AuthDataError
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult

class FirebaseAuthServiceDevelopment : FirebaseAuthService {

    override suspend fun signIn(email: String, password: String): ResultData<FirebaseUser, AuthDataError> {
        val user = FirebaseUser(
            idToken = "token",
            uid = "uid",
            email = email,
            emailVerified = true
        )

        return ResultData.Success(data = user)
    }

    override suspend fun signUp(email: String, password: String): ResultData<FirebaseUser, AuthDataError> {
        val user = FirebaseUser(
            idToken = "token",
            uid = "uid",
            email = email,
            emailVerified = true
        )

        return ResultData.Success(data = user)
    }

    override suspend fun sendEmailVerification(idToken: String): SimpleResult<AuthDataError> {
        return SimpleResult.Success()
    }

    override suspend fun verifyEmail(oobCode: String): ResultData<String, AuthDataError> {
        return ResultData.Success(data = "email")
    }


    override suspend fun requestEmailUpdate(idToken: String, newEmail: String): SimpleResult<AuthDataError> {
        return SimpleResult.Success()
    }

    override suspend fun verifyEmailUpdate(oobCode: String): ResultData<String, AuthDataError> {
        return ResultData.Success(data = "email")
    }


    override suspend fun updatePassword(idToken: String, newPassword: String): SimpleResult<AuthDataError> {
        return SimpleResult.Success()
    }

    override suspend fun requestPasswordReset(email: String): SimpleResult<AuthDataError> {
        return SimpleResult.Success()
    }

    override suspend fun verifyPasswordReset(oobCode: String, newPassword: String): SimpleResult<AuthDataError> {
        return SimpleResult.Success()
    }


    override suspend fun deleteUser(email: String, password: String): SimpleResult<AuthDataError> {
        return SimpleResult.Success()
    }

}