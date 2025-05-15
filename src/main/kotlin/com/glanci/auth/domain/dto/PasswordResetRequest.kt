package com.glanci.auth.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRequest(
    val oobCode: String,
    val newPassword: String
)
