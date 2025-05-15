package com.glanci.auth.domain.model.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseUser(
    val idToken: String,
    val uid: String,
    val email: String,
    val emailVerified: Boolean
)
