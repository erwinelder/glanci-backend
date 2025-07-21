package com.glanci

import com.glanci.core.config.*
import com.glanci.core.routes.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

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
    configureKrpc()
    configureStatusPages()
    configureSecurity()
    configureDI()
    configureRouting()
}
