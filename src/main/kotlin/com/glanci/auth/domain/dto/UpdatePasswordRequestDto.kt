package com.glanci.auth.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequestDto(
    val password: String,
    val newPassword: String
)
