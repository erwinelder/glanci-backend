package com.glanci.category.error

import io.ktor.http.*

sealed class CategoryError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class CategoriesNotSaved : CategoryError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Categories were not saved"
    )

    class CategoriesNotFetched : CategoryError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Categories were not fetched"
    )

}