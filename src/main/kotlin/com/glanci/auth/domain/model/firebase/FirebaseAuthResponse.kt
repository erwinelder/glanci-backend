package com.glanci.auth.domain.model.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseAuthResponse(
    val idToken: String,
    val uid: String,
    val email: String
)
