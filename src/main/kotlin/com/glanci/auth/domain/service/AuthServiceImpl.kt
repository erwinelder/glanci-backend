package com.glanci.auth.domain.service

import com.glanci.auth.data.repository.UserRepository
import com.glanci.auth.shared.dto.CheckAppVersionRequestDto
import com.glanci.auth.shared.dto.UserDto
import com.glanci.auth.shared.dto.UserWithTokenDto
import com.glanci.auth.domain.model.AppVersionValidator
import com.glanci.auth.domain.model.User
import com.glanci.auth.domain.model.UserDataValidator
import com.glanci.auth.domain.model.UserRole
import com.glanci.request.domain.error.AuthDataError
import com.glanci.auth.mapper.toDomainModel
import com.glanci.auth.mapper.toDto
import com.glanci.auth.shared.service.AuthService
import com.glanci.auth.utils.authorizeAtLeastAsUserResult
import com.glanci.auth.utils.createJwtOrNull
import com.glanci.core.domain.model.app.AppLanguage
import com.glanci.core.domain.model.app.AppSubscription
import com.glanci.core.utils.getCurrentTimestamp
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.getDataOrReturn
import com.glanci.request.domain.returnIfError

class AuthServiceImpl(
    private val firebaseAuthService: FirebaseAuthService,
    private val userRepository: UserRepository
) : AuthService {

    private fun getUserWithToken(email: String): ResultData<UserWithTokenDto, AuthDataError> {
        val user = runCatching { userRepository.getUser(email = email) }
            .getOrElse { return ResultData.Error(AuthDataError.UserNotFetched) }
            ?: return ResultData.Error(AuthDataError.UserNotFound)

        val token = createJwtOrNull(user = user) ?: return ResultData.Error(AuthDataError.ErrorDuringCreatingJwtToken)

        return ResultData.Success(data = UserWithTokenDto.fromUserAndToken(user = user, token = token))
    }


    override suspend fun checkTokenValidity(
        appVersion: CheckAppVersionRequestDto,
        token: String
    ): ResultData<UserDto, AuthDataError> {
        val userData = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }

        AppVersionValidator.validateAppVersion(version = appVersion.toDomainModel())
            .returnIfError { return ResultData.Error(it) }

        val user = runCatching { userRepository.getUser(id = userData.id) }
            .getOrElse { return ResultData.Error(AuthDataError.UserNotFetched) }
            ?: return ResultData.Error(AuthDataError.UserNotFound)

        return ResultData.Success(data = user.toDto())
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): ResultData<UserWithTokenDto, AuthDataError> {
        firebaseAuthService.signIn(email = email, password = password).returnIfError { return ResultData.Error(it) }
        return getUserWithToken(email = email)
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        langCode: String
    ): SimpleResult<AuthDataError> {
        val language = AppLanguage.fromLangCode(langCode = langCode)
            ?: return SimpleResult.Error(AuthDataError.InvalidLanguage)

        if (!UserDataValidator.validateName(name)) return SimpleResult.Error(AuthDataError.InvalidName)
        if (!UserDataValidator.validateEmail(email)) return SimpleResult.Error(AuthDataError.InvalidEmail)
        if (!UserDataValidator.validatePassword(password)) return SimpleResult.Error(AuthDataError.InvalidPassword)

        val firebaseUser = firebaseAuthService.signUp(email = email, password = password)
            .getDataOrReturn { return SimpleResult.Error(it) }

        val timestamp = getCurrentTimestamp()
        val user = User(
            email = email,
            role = UserRole.User,
            name = name,
            language = language,
            subscription = AppSubscription.Base,
            timestamp = timestamp
        )

        runCatching { userRepository.createUser(user = user) }
            .getOrElse { return SimpleResult.Error(AuthDataError.UserNotCreated) }

        return firebaseAuthService.sendEmailVerification(idToken = firebaseUser.idToken)
    }

    override suspend fun finishSignUp(oobCode: String): ResultData<UserWithTokenDto, AuthDataError> {
        val email = firebaseAuthService.verifyEmail(oobCode = oobCode).getDataOrReturn { return ResultData.Error(it) }
        return getUserWithToken(email = email)
    }

    override suspend fun requestEmailUpdate(
        password: String,
        newEmail: String,
        token: String
    ): SimpleResult<AuthDataError> {
        val userData = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        val user = runCatching { userRepository.getUser(id = userData.id) }
            .getOrElse { return SimpleResult.Error(AuthDataError.UserNotFetched) }
            ?: return SimpleResult.Error(AuthDataError.UserNotFound)

        val firebaseUser = firebaseAuthService.signIn(email = user.email, password = password)
            .getDataOrReturn { return SimpleResult.Error(it) }
        return firebaseAuthService.requestEmailUpdate(idToken = firebaseUser.idToken, newEmail = newEmail)
    }

    override suspend fun verifyEmailUpdate(
        oobCode: String,
        token: String
    ): ResultData<UserWithTokenDto, AuthDataError> {
        val userData = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }

        val email = firebaseAuthService.verifyEmailUpdate(oobCode = oobCode)
            .getDataOrReturn { return ResultData.Error(it) }

        runCatching { userRepository.saveUserEmail(userId = userData.id, email = email) }
            .onFailure { return ResultData.Error(AuthDataError.UserEmailNotSaved) }

        val user = runCatching { userRepository.getUser(id = userData.id) }
            .getOrElse { return ResultData.Error(AuthDataError.UserNotFetched) }
            ?: return ResultData.Error(AuthDataError.UserNotFound)
        val token = createJwtOrNull(user = user) ?: return ResultData.Error(AuthDataError.ErrorDuringCreatingJwtToken)

        return ResultData.Success(data = UserWithTokenDto.fromUserAndToken(user = user, token = token))
    }

    override suspend fun requestPasswordReset(email: String): SimpleResult<AuthDataError> {
        return firebaseAuthService.requestPasswordReset(email = email)
    }

    override suspend fun verifyPasswordReset(oobCode: String, newPassword: String): SimpleResult<AuthDataError> {
        return firebaseAuthService.verifyPasswordReset(oobCode = oobCode, newPassword = newPassword)
    }

    override suspend fun updatePassword(
        password: String,
        newPassword: String,
        token: String
    ): SimpleResult<AuthDataError> {
        val userData = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        val user = runCatching { userRepository.getUser(id = userData.id) }
            .getOrElse { return SimpleResult.Error(AuthDataError.UserNotFetched) }
            ?: return SimpleResult.Error(AuthDataError.UserNotFound)

        val firebaseUser = firebaseAuthService.signIn(email = user.email, password = password)
            .getDataOrReturn { return SimpleResult.Error(it) }
        return firebaseAuthService.updatePassword(idToken = firebaseUser.idToken, newPassword = newPassword)
    }

    override suspend fun saveUserName(name: String, token: String): SimpleResult<AuthDataError> {
        val userData = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        if (!UserDataValidator.validateName(name)) return SimpleResult.Error(AuthDataError.InvalidName)

        runCatching {
            userRepository.saveUserName(userId = userData.id, name = name)
        }
            .onFailure { return SimpleResult.Error(AuthDataError.UserNameNotSaved) }

        return SimpleResult.Success()
    }

    override suspend fun saveUserLanguage(langCode: String, timestamp: Long, token: String): SimpleResult<AuthDataError> {
        val userData = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }
        val language = AppLanguage.fromLangCode(langCode = langCode)
            ?: return SimpleResult.Error(AuthDataError.InvalidLanguage)

        runCatching {
            userRepository.saveUserLanguage(userId = userData.id, language = language, timestamp = timestamp)
        }
            .onFailure { return SimpleResult.Error(AuthDataError.UserLanguageNotSaved) }

        return SimpleResult.Success()
    }

    override suspend fun deleteAccount(email: String, password: String, token: String): SimpleResult<AuthDataError> {
        val userData = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        runCatching { userRepository.deleteUser(userId = userData.id) }
            .onFailure { return SimpleResult.Error(AuthDataError.UserNotDeleted) }

        return firebaseAuthService.deleteUser(email = email, password = password)
    }

}