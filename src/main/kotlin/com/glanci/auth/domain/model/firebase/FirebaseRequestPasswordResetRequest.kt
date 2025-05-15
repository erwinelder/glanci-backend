package com.glanci.auth.domain.model.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseRequestPasswordResetRequest(
    val requestType: String = "PASSWORD_RESET",
    val email: String
)
