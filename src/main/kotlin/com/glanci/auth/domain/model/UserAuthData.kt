package com.glanci.auth.domain.model

import com.glanci.auth.shared.dto.UserRole

data class UserAuthData(
    val id: Int,
    val role: UserRole
)
