package com.glanci.auth.domain.model.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseVerifyAndChangeEmailRequest(
    val requestType: String = "VERIFY_AND_CHANGE_EMAIL",
    val idToken: String,
    val email: String
)
