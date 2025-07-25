package com.glanci.utils

import com.glanci.auth.domain.model.User
import com.glanci.auth.domain.model.UserRole
import com.glanci.auth.utils.createJwtOrNull
import com.glanci.core.config.configureKrpc
import com.glanci.core.domain.model.app.AppLanguage
import com.glanci.core.domain.model.app.AppSubscription
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc


fun getJwt(userId: Int, role: UserRole): String {
    return createJwtOrNull(
        user = User(
            id = userId,
            email = "example@domain.com",
            role = role,
            name = "User",
            language = AppLanguage.English,
            subscription = AppSubscription.Base,
            timestamp = 0
        )
    )!!
}


fun ApplicationTestBuilder.getKrpcClient(): HttpClient {
    return createClient {
        installKrpc {
            waitForServices = true
        }
    }
}

fun HttpClient.configureRcp(path: String): KtorRpcClient {
    return rpc {
        url {
            host = "localhost"
            port = 80
            encodedPath = path
        }
        configureKrpc()
    }
}
