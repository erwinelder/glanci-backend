package com.glanci.core.routes

import com.glanci.auth.routes.authRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting() {
    routing {
        coreRoutes()
        authRoutes(
            firebaseAuthService = this@configureRouting.get(),
            userService = this@configureRouting.get()
        )
    }
}
