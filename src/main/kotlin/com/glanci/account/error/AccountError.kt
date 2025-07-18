package com.glanci.account.error

import io.ktor.http.*

sealed class AccountError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class AccountsNotSaved : AccountError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Accounts were not saved"
    )

    class AccountsNotFetched : AccountError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Accounts were not fetched"
    )

}