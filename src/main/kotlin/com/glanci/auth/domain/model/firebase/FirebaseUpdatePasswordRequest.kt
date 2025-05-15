package com.glanci.auth.domain.model.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseUpdatePasswordRequest(
    val idToken: String,
    val password: String,
    val returnSecureToken: Boolean = true
)