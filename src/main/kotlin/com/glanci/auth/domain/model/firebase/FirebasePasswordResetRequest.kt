package com.glanci.auth.domain.model.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebasePasswordResetRequest(
    val oobCode: String,
    val newPassword: String
)
