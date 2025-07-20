package com.glanci.budget.error

import io.ktor.http.*

sealed class BudgetError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class BudgetsNotSaved : BudgetError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Budgets were not saved"
    )

    class BudgetsNotFetched : BudgetError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Budgets were not fetched"
    )

}