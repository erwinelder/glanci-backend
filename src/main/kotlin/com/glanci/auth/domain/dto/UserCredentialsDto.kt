package com.glanci.auth.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentialsDto(
    val email: String,
    val password: String
)