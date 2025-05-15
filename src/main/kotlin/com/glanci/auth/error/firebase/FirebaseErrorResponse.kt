package com.glanci.auth.error.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseErrorResponse(
    val error: FirebaseErrorDetail
)