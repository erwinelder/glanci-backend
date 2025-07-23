package com.glanci.request.domain

import com.glanci.request.domain.error.RootError
import com.glanci.request.domain.success.RootSuccess
import kotlinx.serialization.Serializable

@Serializable
sealed interface ResultData<out D, out E : RootError> {

    @Serializable
    data class Success<out D, out E : RootError>(val data: D): ResultData<D, E>

    @Serializable
    data class Error<out D, out E : RootError>(val error: E): ResultData<D, E>


    fun getDataOrNull(): D? = (this as? Success)?.data
    fun getErrorOrNull(): E? = (this as? Error)?.error

    fun <R> mapData(transform: (D) -> R): ResultData<R, E> {
        return when (this) {
            is Success -> Success<R, E>(this.data.let(transform))
            is Error -> Error(this.error)
        }
    }

    fun mapDataToUnit(): ResultData<Unit, E> {
        return when (this) {
            is Success -> Success(Unit)
            is Error -> Error(this.error)
        }
    }

    fun <R : RootError> mapError(transform: (E) -> R): ResultData<D, R> {
        return when (this) {
            is Success -> Success(this.data)
            is Error -> Error<D, R>(this.error.let(transform))
        }
    }

    fun <S : RootSuccess?> toDefaultResult(success: S): Result<S, E> {
        return when (this) {
            is Success -> Result.Success(success)
            is Error -> Result.Error(this.error)
        }
    }

}

inline fun <D, E : RootError> ResultData<D, E>.getDataOrReturn(onReturn: (E) -> Nothing): D {
    return when (this) {
        is ResultData.Success -> this.data
        is ResultData.Error -> onReturn(this.error)
    }
}

inline fun <D, E : RootError> ResultData<D, E>.returnIfError(onReturn: (E) -> Nothing) {
    if (this is ResultData.Error) onReturn(this.error)
}
