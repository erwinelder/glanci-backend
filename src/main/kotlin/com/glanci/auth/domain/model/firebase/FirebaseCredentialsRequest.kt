package com.glanci.auth.domain.model.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseCredentialsRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)
