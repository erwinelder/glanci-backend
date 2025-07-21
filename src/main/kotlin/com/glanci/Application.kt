package com.glanci

import com.glanci.account.di.accountModule
import com.glanci.auth.di.authModule
import com.glanci.budget.di.budgetModule
import com.glanci.category.di.categoryModule
import com.glanci.categoryCollection.di.categoryCollectionModule
import com.glanci.core.config.*
import com.glanci.core.di.coreModule
import com.glanci.core.routes.configureRouting
import com.glanci.navigation.di.navigationButtonModule
import com.glanci.personalization.di.personalizationModule
import com.glanci.record.di.recordModule
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.rpc.krpc.ktor.server.Krpc

fun main() {
    embeddedServer(
        factory = Netty,
        port = System.getenv("PORT")?.toIntOrNull() ?: 8080,
        host = "0.0.0.0",
        module = Application::mainModule
    ).start(wait = true)
}

fun Application.mainModule() {
    configureSerialization()
    configureHTTP()
    install(Krpc)
    configureStatusPages()
    configureSecurity()
    configureDI(
        coreModule, authModule,
        accountModule, categoryModule,
        recordModule,
        categoryCollectionModule, budgetModule,
        personalizationModule, navigationButtonModule
    )
    configureRouting()
}
