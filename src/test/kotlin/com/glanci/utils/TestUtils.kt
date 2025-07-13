package com.glanci.utils

import com.glanci.auth.domain.model.User
import com.glanci.auth.domain.model.UserRole
import com.glanci.auth.utils.createJwt
import com.glanci.core.domain.model.app.AppLanguage
import com.glanci.core.domain.model.app.AppSubscription
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder


fun getJwt(userId: Int, role: UserRole): String {
    return createJwt(
        user = User(
            id = userId,
            email = "example@domain.com",
            role = role,
            name = "User",
            language = AppLanguage.English,
            subscription = AppSubscription.Base,
            timestamp = 0
        )
    )
}

fun ApplicationTestBuilder.getClient(): HttpClient {
    return createClient {
        install(ContentNegotiation) {
            json()
        }
    }
}