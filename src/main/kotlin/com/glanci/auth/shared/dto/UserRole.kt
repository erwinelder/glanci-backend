package com.glanci.auth.shared.dto

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    User, Admin
}