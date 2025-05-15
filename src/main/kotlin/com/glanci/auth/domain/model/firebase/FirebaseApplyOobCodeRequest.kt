package com.glanci.auth.domain.model.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseApplyOobCodeRequest(
    val oobCode: String,
    val returnSecureToken: Boolean = true
)
