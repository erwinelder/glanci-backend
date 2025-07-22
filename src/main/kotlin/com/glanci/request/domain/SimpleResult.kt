package com.glanci.request.domain

import kotlinx.serialization.Serializable

@Serializable
sealed interface SimpleResult<out E : RootError> {

    @Serializable
    class Success<out E : RootError>: SimpleResult<E>

    @Serializable
    data class Error<out E : RootError>(val error: E): SimpleResult<E>


    fun getErrorOrNull(): E? = (this as? Error)?.error

}

inline fun <E : RootError> SimpleResult<E>.returnIfError(onReturn: (E) -> Nothing) {
    if (this is SimpleResult.Error) onReturn(this.error)
}

inline fun <E : RootError> SimpleResult<E>.returnItIfError(onReturn: (SimpleResult<E>) -> Nothing) {
    if (this is SimpleResult.Error) onReturn(this)
}
