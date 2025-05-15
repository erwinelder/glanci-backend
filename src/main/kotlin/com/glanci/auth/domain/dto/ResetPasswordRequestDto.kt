package com.glanci.auth.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequestDto(
    val oobCode: String,
    val newPassword: String
)
