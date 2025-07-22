package com.glanci.auth.error

import io.ktor.http.*

sealed class AuthException(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class InvalidToken : AuthException(
        statusCode = HttpStatusCode.Unauthorized,
        message = "Token is missing or invalid"
    )

    class ErrorDuringExtractingJwtSecret : AuthException(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Error during extracting JWT secret"
    )

}