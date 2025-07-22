package com.glanci.request.domain

import kotlinx.serialization.Serializable

@Serializable
sealed interface Result<out S: RootSuccess?, out E: RootError> {

    @Serializable
    data class Success<out S: RootSuccess?, out E: RootError>(val success: S): Result<S, E>

    @Serializable
    data class Error<out S: RootSuccess?, out E: RootError>(val error: E): Result<S, E>

}