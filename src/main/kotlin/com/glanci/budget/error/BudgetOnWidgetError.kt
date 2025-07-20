package com.glanci.budget.error

import io.ktor.http.*

sealed class BudgetOnWidgetError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class BudgetsOnWidgetNotSaved : BudgetOnWidgetError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Budgets on widget were not saved"
    )

    class BudgetsOnWidgetNotFetched : BudgetOnWidgetError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Budgets on widget were not fetched"
    )

}