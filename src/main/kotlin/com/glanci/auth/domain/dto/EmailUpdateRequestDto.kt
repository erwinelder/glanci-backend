package com.glanci.auth.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmailUpdateRequestDto(
    val password: String,
    val newEmail: String
)
