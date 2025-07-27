package com.glanci.auth.shared.dto

import com.glanci.auth.domain.model.User
import com.glanci.auth.domain.model.UserRole
import com.glanci.core.domain.model.app.AppSubscription
import kotlinx.serialization.Serializable

@Serializable
data class UserWithTokenDto(
    val id: Int = 0,
    val email: String,
    val role: UserRole,
    val name: String,
    val langCode: String,
    val subscription: AppSubscription,
    val timestamp: Long,
    val token: String
) {
    companion object {

        fun fromUserAndToken(user: User, token: String): UserWithTokenDto {
            return UserWithTokenDto(
                id = user.id,
                email = user.email,
                role = user.role,
                name = user.name,
                langCode = user.language.langCode,
                subscription = user.subscription,
                timestamp = user.timestamp,
                token = token
            )
        }

    }
}