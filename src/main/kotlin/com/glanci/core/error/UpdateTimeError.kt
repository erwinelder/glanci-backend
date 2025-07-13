package com.glanci.core.error

import io.ktor.http.HttpStatusCode

sealed class UpdateTimeError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class UpdateTimeNotFetched : UpdateTimeError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Update time was not fetched"
    )

    class UpdateTimeNotSaved : UpdateTimeError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Update time was not saved"
    )

    class UpdateTimeNotFound : UpdateTimeError(
        statusCode = HttpStatusCode.NotFound,
        message = "Update time was not found"
    )

}