package com.glanci.request.domain

import com.glanci.request.domain.error.DataError
import kotlinx.serialization.Serializable

@Serializable
sealed interface SimpleResult<out E : DataError> {

    @Serializable
    class Success<out E : DataError>: SimpleResult<E>

    @Serializable
    data class Error<out E : DataError>(val error: E): SimpleResult<E>


    fun getErrorOrNull(): E? = (this as? Error)?.error

}

inline fun <E : DataError> SimpleResult<E>.returnIfError(onReturn: (E) -> Nothing) {
    if (this is SimpleResult.Error) onReturn(this.error)
}

inline fun <E : DataError> SimpleResult<E>.returnItIfError(onReturn: (SimpleResult<E>) -> Nothing) {
    if (this is SimpleResult.Error) onReturn(this)
}
