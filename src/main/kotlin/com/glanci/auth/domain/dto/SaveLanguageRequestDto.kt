package com.glanci.auth.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class SaveLanguageRequestDto(
    val langCode: String,
    val timestamp: Long
)
