package com.glanci.auth.error.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseInnerError(
    val message: String,
    val domain: String,
    val reason: String
)