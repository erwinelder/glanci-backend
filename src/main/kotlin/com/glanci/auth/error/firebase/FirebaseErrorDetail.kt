package com.glanci.auth.error.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseErrorDetail(
    val code: Int,
    val message: String,
    val errors: List<FirebaseInnerError>
)