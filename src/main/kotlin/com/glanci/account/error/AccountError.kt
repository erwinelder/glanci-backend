package com.glanci.account.error

import io.ktor.http.HttpStatusCode

sealed class AccountError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class AccountsAreMissingOrInvalid : AccountError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Accounts are missing or invalid"
    )

    class TimestampIsMissingOrInvalid : AccountError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Timestamp is missing or invalid"
    )

    class AccountsNotSaved : AccountError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Accounts were not saved"
    )

    class AccountsNotFetched : AccountError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Accounts were not fetched"
    )

    class AccountsNotFound : AccountError(
        statusCode = HttpStatusCode.NotFound,
        message = "Accounts were not found"
    )

}