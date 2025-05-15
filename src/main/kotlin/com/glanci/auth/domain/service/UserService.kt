package com.glanci.auth.domain.service

import com.glanci.auth.data.repository.UserRepository
import com.glanci.auth.domain.model.User
import com.glanci.auth.domain.model.UserRole
import com.glanci.auth.error.UserError
import com.glanci.core.domain.AppLanguage
import com.glanci.core.domain.AppSubscription

class UserService(
    private val userRepository: UserRepository
) {

    fun getUser(id: Int): User {
        return runCatching { userRepository.getUser(id = id) }
            .onFailure { throw UserError.UserNotFetched() }
            .getOrNull()
            ?: throw UserError.UserNotFound()
    }

    fun getUser(email: String): User {
        return runCatching { userRepository.getUser(email = email) }
            .onFailure { throw UserError.UserNotFetched() }
            .getOrNull()
            ?: throw UserError.UserNotFound()
    }

    fun getUserOrNull(id: Int): User? {
        return runCatching { userRepository.getUser(id = id) }
            .onFailure { throw UserError.UserNotFetched() }
            .getOrNull()
    }

    fun getUserOrNull(email: String): User? {
        return runCatching { userRepository.getUser(email = email) }
            .onFailure { throw UserError.UserNotFetched() }
            .getOrNull()
    }

    fun searchUsers(query: String): List<User> {
        return try {
            userRepository.searchUsers(query = query)
        } catch (_: Exception) {
            throw UserError.UsersNotFound()
        }
    }

    fun getAllUsers(): List<User> {
        return try {
            userRepository.getAllUsers()
        } catch (_: Exception) {
            throw UserError.UsersNotFetched()
        }
    }

    fun saveUserName(userId: Int, name: String) {
        try {
            userRepository.saveUserName(userId = userId, name = name)
        } catch (_: Exception) {
            throw UserError.UserNameNotSaved()
        }
    }

    fun saveUserEmail(userId: Int, email: String) {
        try {
            userRepository.saveUserEmail(userId = userId, email = email)
        } catch (_: Exception) {
            throw UserError.UserEmailNotSaved()
        }
    }

    fun saveUserLanguage(userId: Int, lang: AppLanguage) {
        try {
            userRepository.saveUserLanguage(userId = userId, language = lang)
        } catch (_: Exception) {
            throw UserError.UserLanguageNotSaved()
        }
    }

    fun saveUserSubscription(userId: Int, subscription: AppSubscription) {
        try {
            userRepository.saveUserSubscription(userId = userId, subscription = subscription)
        } catch (_: Exception) {
            throw UserError.UserSubscriptionNotSaved()
        }
    }

    fun createUser(email: String, name: String, language: AppLanguage): User {
        try {
            val user = User(
                email = email,
                role = UserRole.User,
                name = name,
                language = language,
                subscription = AppSubscription.Free
            )
            val id = userRepository.createUser(user = user)
            return user.copy(id = id)
        } catch (_: Exception) {
            throw UserError.UserNotCreated()
        }
    }

    fun deleteUser(userId: Int) {
        try {
            userRepository.deleteUser(userId = userId)
        } catch (_: Exception) {
            throw UserError.UserNotDeleted()
        }
    }

}