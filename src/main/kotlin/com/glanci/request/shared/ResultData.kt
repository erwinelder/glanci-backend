package com.glanci.request.shared

import com.glanci.request.shared.error.DataError
import com.glanci.request.shared.success.DataSuccess
import kotlinx.serialization.Serializable

@Serializable
sealed interface ResultData<out D, out E : DataError> {

    @Serializable
    data class Success<out D, out E : DataError>(val data: D): ResultData<D, E>

    @Serializable
    data class Error<out D, out E : DataError>(val error: E): ResultData<D, E>


    fun getDataOrNull(): D? = (this as? Success)?.data
    fun getErrorOrNull(): E? = (this as? Error)?.error

    fun <R> mapData(transform: (D) -> R): ResultData<R, E> {
        return when (this) {
            is Success -> Success<R, E>(this.data.let(transform))
            is Error -> Error(this.error)
        }
    }

    fun <S : DataSuccess?> toDefaultResult(success: S): Result<S, E> {
        return when (this) {
            is Success -> Result.Success(success)
            is Error -> Result.Error(this.error)
        }
    }

}

inline fun <D, E : DataError> ResultData<D, E>.getOrElse(action: (E) -> Nothing): D {
    return when (this) {
        is ResultData.Success -> this.data
        is ResultData.Error -> action(this.error)
    }
}

inline fun <D, E : DataError> ResultData<D, E>.onError(action: (E) -> Nothing) {
    if (this is ResultData.Error) action(this.error)
}
