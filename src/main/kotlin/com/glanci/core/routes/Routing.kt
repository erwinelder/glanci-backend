package com.glanci.core.routes

import com.glanci.account.shared.service.AccountService
import com.glanci.auth.routes.authRoutes
import com.glanci.category.shared.service.CategoryService
import com.glanci.core.config.configureKrpc
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
    }
}
