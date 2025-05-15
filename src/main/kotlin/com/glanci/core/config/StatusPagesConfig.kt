package com.glanci.core.config

import com.glanci.auth.error.AuthError
import com.glanci.auth.error.UserError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages(
    configureCustomStatusPages: (StatusPagesConfig.() -> Unit)? = null
) {
    install(StatusPages) {

        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }

        exception<AuthError> { call, cause ->
            call.respondText(
                text = "Auth error ${cause.statusCode.value}: ${cause.message}",
                status = cause.statusCode
            )
        }

        exception<UserError> { call, cause ->
            call.respondText(
                text = "User error ${cause.statusCode.value}: ${cause.message}",
                status = cause.statusCode
            )
        }

        configureCustomStatusPages?.invoke(this)

    }
}