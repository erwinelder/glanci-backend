package com.glanci.auth.error

import io.ktor.http.*

sealed class UserError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class UserNotFound : UserError(
        statusCode = HttpStatusCode.NotFound,
        message = "User was not found"
    )

    class UsersNotFound : UserError(
        statusCode = HttpStatusCode.NotFound,
        message = "Users were not found"
    )

    class UserNotFetched : UserError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "User was not fetched"
    )

    class UsersNotFetched : UserError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Users were not fetched"
    )

    class UserNotCreated : UserError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "User was not created"
    )

    class UserNameNotSaved : UserError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "User name was not saved"
    )

    class UserEmailNotSaved : UserError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "User email was not saved"
    )

    class UserLanguageNotSaved : UserError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "User language was not saved"
    )

    class UserSubscriptionNotSaved : UserError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "User subscription was not saved"
    )

    class UserNotDeleted : UserError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "User was not deleted"
    )

}