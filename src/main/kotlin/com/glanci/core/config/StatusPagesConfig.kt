package com.glanci.core.config

import com.glanci.account.error.AccountError
import com.glanci.auth.error.AuthError
import com.glanci.auth.error.UserError
import com.glanci.budget.error.BudgetError
import com.glanci.budget.error.BudgetOnWidgetError
import com.glanci.category.error.CategoryError
import com.glanci.categoryCollection.error.CategoryCollectionError
import com.glanci.core.error.UpdateTimeError
import com.glanci.navigation.error.NavigationButtonError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages(
    configureCustomStatusPages: (StatusPagesConfig.() -> Unit)? = null
) {
    install(StatusPages) {

        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }

        exception<AuthError> { call, cause ->
            call.respondText(
                text = "Auth error ${cause.statusCode.value}: ${cause.message}",
                status = cause.statusCode
            )
        }

        exception<UserError> { call, cause ->
            call.respondText(
                text = "User error ${cause.statusCode.value}: ${cause.message}",
                status = cause.statusCode
            )
        }

        exception<UpdateTimeError> { call, cause ->
            call.respondText(
                text = "UpdateTime error ${cause.statusCode.value}: ${cause.message}",
                status = cause.statusCode
            )
        }

        exception<AccountError> { call, cause ->
            call.respondText(
                text = "Account error ${cause.statusCode.value}: ${cause.message}",
                status = cause.statusCode
            )
        }

        exception<CategoryError> { call, cause ->
            call.respondText(
                text = "Category error ${cause.statusCode.value}: ${cause.message}",
                status = cause.statusCode
            )
        }

        exception<CategoryCollectionError> { call, cause ->
            call.respondText(
                text = "CategoryCollection error ${cause.statusCode.value}: ${cause.message}",
                status = cause.statusCode
            )
        }

        exception<BudgetError> { call, cause ->
            call.respondText(
                text = "Budget error ${cause.statusCode.value}: ${cause.message}",
                status = cause.statusCode
            )
        }

        exception<BudgetOnWidgetError> { call, cause ->
            call.respondText(
                text = "BudgetOnWidget error ${cause.statusCode.value}: ${cause.message}",
                status = cause.statusCode
            )
        }

        exception<NavigationButtonError> { call, cause ->
            call.respondText(
                text = "NavigationButton error ${cause.statusCode.value}: ${cause.message}",
                status = cause.statusCode
            )
        }

        configureCustomStatusPages?.invoke(this)

    }
}