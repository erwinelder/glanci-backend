package com.glanci.request.domain

import com.glanci.request.domain.error.DataError
import com.glanci.request.domain.success.RootSuccess
import kotlinx.serialization.Serializable

@Serializable
sealed interface Result<out S: RootSuccess?, out E: DataError> {

    @Serializable
    data class Success<out S: RootSuccess?, out E: DataError>(val success: S): Result<S, E>

    @Serializable
    data class Error<out S: RootSuccess?, out E: DataError>(val error: E): Result<S, E>

}