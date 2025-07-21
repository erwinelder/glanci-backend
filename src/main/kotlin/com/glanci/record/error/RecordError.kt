package com.glanci.record.error

import io.ktor.http.*

sealed class RecordError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class RecordsWithItemsNotSaved : RecordError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Records with items were not saved"
    )

    class RecordsWithItemsNotFetched : RecordError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Records with items were not fetched"
    )

}