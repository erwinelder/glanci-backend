package com.glanci.auth.domain.service

import com.glanci.auth.domain.model.firebase.FirebaseUser
import com.glanci.request.shared.error.AuthDataError
import com.glanci.request.shared.ResultData
import com.glanci.request.shared.SimpleResult

class FirebaseAuthServiceDevelopment : FirebaseAuthService {

    val users = mutableListOf(
        "base_user@domain.com",
        "premium_user@domain.com",
        "admin@domain.com",
        "new_user@domain.com"
    )

    override suspend fun signIn(email: String, password: String): ResultData<FirebaseUser, AuthDataError> {
        if (email !in users) {
            return ResultData.Error(error = AuthDataError.InvalidCredentials)
        }

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

        users.add(email)

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
        if (email !in users) return SimpleResult.Error(error = AuthDataError.InvalidCredentials)

        users.remove(email)

        return SimpleResult.Success()
    }

}