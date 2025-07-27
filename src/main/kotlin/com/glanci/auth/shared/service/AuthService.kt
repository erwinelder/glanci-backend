package com.glanci.auth.shared.service

import com.glanci.auth.shared.dto.CheckAppVersionRequestDto
import com.glanci.auth.shared.dto.UserDto
import com.glanci.auth.shared.dto.UserWithTokenDto
import com.glanci.request.domain.error.AuthError
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import kotlinx.rpc.annotations.Rpc

@Rpc
interface AuthService {

    suspend fun checkTokenValidity(appVersion: CheckAppVersionRequestDto, token: String): ResultData<UserDto, AuthError>

    suspend fun signIn(email: String, password: String): ResultData<UserWithTokenDto, AuthError>

    suspend fun signUp(name: String, email: String, password: String, langCode: String): SimpleResult<AuthError>

    suspend fun finishSignUp(oobCode: String): ResultData<UserWithTokenDto, AuthError>

    suspend fun requestEmailUpdate(password: String, newEmail: String, token: String): SimpleResult<AuthError>

    suspend fun verifyEmailUpdate(oobCode: String, token: String): ResultData<UserWithTokenDto, AuthError>

    suspend fun requestPasswordReset(email: String): SimpleResult<AuthError>

    suspend fun verifyPasswordReset(oobCode: String, newPassword: String): SimpleResult<AuthError>

    suspend fun updatePassword(password: String, newPassword: String, token: String): SimpleResult<AuthError>

    suspend fun saveUserName(name: String, token: String): SimpleResult<AuthError>

    suspend fun saveUserLanguage(langCode: String, timestamp: Long, token: String): SimpleResult<AuthError>

    suspend fun deleteAccount(email: String, password: String, token: String): SimpleResult<AuthError>

}