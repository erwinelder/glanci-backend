package com.glanci.core.routes

import com.glanci.account.shared.service.AccountService
import com.glanci.auth.shared.service.AuthService
import com.glanci.budget.shared.service.BudgetOnWidgetService
import com.glanci.budget.shared.service.BudgetService
import com.glanci.category.shared.service.CategoryService
import com.glanci.categoryCollection.shared.service.CategoryCollectionService
import com.glanci.navigation.shared.service.NavigationButtonService
import com.glanci.personalization.shared.service.WidgetService
import com.glanci.record.shared.service.RecordService
import com.glanci.transfer.shared.service.TransferService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.rpc
import org.koin.ktor.ext.get

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Welcome to Glanci!")
        }
        rpc("/auth") {
            registerService<AuthService> { this@configureRouting.get() }
        }
        rpc("/account") {
            registerService<AccountService> { this@configureRouting.get() }
        }
        rpc("/category") {
            registerService<CategoryService> { this@configureRouting.get() }
        }
        rpc("/record") {
            registerService<RecordService> { this@configureRouting.get() }
        }
        rpc("/transfer") {
            registerService<TransferService> { this@configureRouting.get() }
        }
        rpc("/categoryCollection") {
            registerService<CategoryCollectionService> { this@configureRouting.get() }
        }
        rpc("/budget") {
            registerService<BudgetService> { this@configureRouting.get() }
            registerService<BudgetOnWidgetService> { this@configureRouting.get() }
        }
        rpc("/personalization") {
            registerService<WidgetService> { this@configureRouting.get() }
        }
        rpc("/navigation") {
            registerService<NavigationButtonService> { this@configureRouting.get() }
        }
    }
}
