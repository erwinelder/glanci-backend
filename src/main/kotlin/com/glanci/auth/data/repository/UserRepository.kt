package com.glanci.auth.data.repository

import com.glanci.auth.domain.model.User
import com.glanci.core.domain.model.app.AppLanguage
import com.glanci.core.domain.model.app.AppSubscription

interface UserRepository {

    fun getUser(id: Int): User?

    fun getUser(email: String): User?

    fun searchUsers(query: String): List<User>

    fun getAllUsers(): List<User>

    fun createUser(user: User): Int

    fun saveUserName(userId: Int, name: String)

    fun saveUserLanguage(userId: Int, language: AppLanguage, timestamp: Long)

    fun saveUserEmail(userId: Int, email: String)

    fun saveUserSubscription(userId: Int, subscription: AppSubscription)

    fun deleteUser(userId: Int)

}