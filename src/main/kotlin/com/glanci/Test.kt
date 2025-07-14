package com.glanci

import com.glanci.account.shared.service.AccountService
import com.glanci.auth.domain.model.User
import com.glanci.auth.domain.model.UserRole
import com.glanci.auth.utils.createJwt
import com.glanci.core.domain.model.app.AppLanguage
import com.glanci.core.domain.model.app.AppSubscription
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService

fun main() = runBlocking {
    val ktorClient = HttpClient {
        installKrpc()
    }

    val client = ktorClient.rpc {
        url {
            host = "localhost"
            port = 8080
            encodedPath = "account"
        }

        rpcConfig {
            serialization {
                json()
            }
        }
    }

    val service = client.withService<AccountService>()

    val token = createJwt(
        user = User(
            id = 1,
            email = "example@domain.com",
            role = UserRole.User,
            name = "Test User",
            language = AppLanguage.English,
            subscription = AppSubscription.Base,
            timestamp = 0
        )
    )

    val result = service.getUpdateTime(token = token)
    println("Result: $result")

    ktorClient.close()
}