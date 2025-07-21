package com.glanci.core.routes

import com.glanci.account.shared.service.AccountService
import com.glanci.auth.routes.authRoutes
import com.glanci.budget.shared.service.BudgetOnWidgetService
import com.glanci.budget.shared.service.BudgetService
import com.glanci.category.shared.service.CategoryService
import com.glanci.categoryCollection.shared.service.CategoryCollectionService
import com.glanci.core.config.configureKrpc
import com.glanci.navigation.shared.service.NavigationButtonService
import com.glanci.personalization.shared.service.WidgetService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.rpc
import org.koin.ktor.ext.get

fun Application.configureRouting() {
    routing {
        coreRoutes()
        authRoutes(
            firebaseAuthService = this@configureRouting.get(),
            userService = this@configureRouting.get()
        )
        rpc("/account") {
            configureKrpc()
            registerService<AccountService> { this@configureRouting.get() }
        }
        rpc("/category") {
            configureKrpc()
            registerService<CategoryService> { this@configureRouting.get() }
        }
        rpc("/categoryCollection") {
            configureKrpc()
            registerService<CategoryCollectionService> { this@configureRouting.get() }
        }
        rpc("/budget") {
            configureKrpc()
            registerService<BudgetService> { this@configureRouting.get() }
            registerService<BudgetOnWidgetService> { this@configureRouting.get() }
        }
        rpc("/personalization") {
            configureKrpc()
            registerService<WidgetService> { this@configureRouting.get() }
        }
        rpc("/navigation") {
            configureKrpc()
            registerService<NavigationButtonService> { this@configureRouting.get() }
        }
    }
}
