package com.glanci.auth.domain.model

import com.glanci.core.domain.model.app.AppLanguage
import com.glanci.core.domain.model.app.AppSubscription

data class User(
    val id: Int = 0,
    val email: String,
    val role: UserRole,
    val name: String,
    val language: AppLanguage,
    val subscription: AppSubscription,
    val timestamp: Long
)
