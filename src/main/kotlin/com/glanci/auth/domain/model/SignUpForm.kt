package com.glanci.auth.domain.model

import com.glanci.core.domain.model.app.AppLanguage

data class SignUpForm(
    val name: String,
    val email: String,
    val password: String,
    val language: AppLanguage
)
