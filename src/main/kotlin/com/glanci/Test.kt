package com.glanci

import com.glanci.account.shared.service.AccountService
import com.glanci.auth.domain.model.User
import com.glanci.auth.domain.model.UserRole
import com.glanci.auth.utils.createJwt
import com.glanci.core.domain.model.app.AppLanguage
import com.glanci.core.domain.model.app.AppSubscription
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService

fun main() = runBlocking {
    val ktorClient = HttpClient(OkHttp) {
        installKrpc()
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }

    val client = ktorClient.rpc {
        url {
            protocol = URLProtocol.WSS
            host = "walletglance-backend-hgddf5fwckbqf8bf.northeurope-01.azurewebsites.net"
            port = 443
            encodedPath = "account"
        }

        rpcConfig {
            serialization { json() }
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