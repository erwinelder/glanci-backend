package com.glanci.auth.domain.model.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseVerifyEmailRequest(
    val requestType: String = "VERIFY_EMAIL",
    val idToken: String
)
