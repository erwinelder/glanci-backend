package com.glanci.auth.shared.dto

import com.glanci.auth.domain.model.UserRole
import com.glanci.core.domain.model.app.AppSubscription
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val role: UserRole,
    val name: String,
    val langCode: String,
    val subscription: AppSubscription,
    val timestamp: Long
)