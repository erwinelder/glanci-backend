package com.glanci.transfer.error

import io.ktor.http.*

sealed class TransferError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class TransfersNotSaved : TransferError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Transfers were not saved"
    )

    class TransfersNotFetched : TransferError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Transfers were not fetched"
    )

}