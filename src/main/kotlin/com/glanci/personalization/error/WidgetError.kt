package com.glanci.personalization.error

import io.ktor.http.*

sealed class WidgetError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class WidgetsNotSaved : WidgetError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Widgets were not saved"
    )

    class WidgetsNotFetched : WidgetError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Widgets were not fetched"
    )

}