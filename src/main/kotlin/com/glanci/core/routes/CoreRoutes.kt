package com.glanci.core.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.coreRoutes() {
    get("/") {
        call.respondText("Welcome to Glanci!")
    }
}