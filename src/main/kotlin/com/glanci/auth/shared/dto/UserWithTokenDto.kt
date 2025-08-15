package com.glanci.auth.shared.dto

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
)
