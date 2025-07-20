package com.glanci.navigation.error

import io.ktor.http.*

sealed class NavigationButtonError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class NavigationButtonsNotSaved : NavigationButtonError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "NavigationButtons were not saved"
    )

    class NavigationButtonsNotFetched : NavigationButtonError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "NavigationButtons were not fetched"
    )

}