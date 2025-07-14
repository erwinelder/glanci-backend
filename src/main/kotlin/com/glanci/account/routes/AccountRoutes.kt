package com.glanci.account.routes

import com.glanci.account.shared.service.AccountService
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json

fun Routing.accountRoutes(
    accountService: AccountService
) {
    rpc("/account") {
        rpcConfig {
            serialization {
                json()
            }
        }
        registerService<AccountService> { accountService }
    }
}