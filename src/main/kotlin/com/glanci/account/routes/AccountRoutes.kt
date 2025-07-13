package com.glanci.account.routes

import com.glanci.account.shared.service.AccountService
import com.glanci.core.config.configureKrpc
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.rpc

fun Routing.accountRoutes(
    accountService: AccountService
) {
    rpc("/account") {
        configureKrpc()
        registerService<AccountService> { accountService }
    }
}