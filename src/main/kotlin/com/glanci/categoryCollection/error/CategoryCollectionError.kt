package com.glanci.categoryCollection.error

import io.ktor.http.*

sealed class CategoryCollectionError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class CategoryCollectionsNotSaved : CategoryCollectionError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Category collections were not saved"
    )

    class CategoryCollectionsNotFetched : CategoryCollectionError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Category collections were not fetched"
    )

}