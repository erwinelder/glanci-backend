package com.glanci.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    User, Admin
}