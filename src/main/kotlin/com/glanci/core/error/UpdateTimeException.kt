package com.glanci.core.error

import io.ktor.http.*

@Deprecated("")
sealed class UpdateTimeException(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class UpdateTimeNotFetched : UpdateTimeException(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Update time was not fetched"
    )

    class UpdateTimeNotSaved : UpdateTimeException(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Update time was not saved"
    )

}