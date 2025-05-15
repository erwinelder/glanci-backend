package com.glanci.core.routes

import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.coreRoutes() {
    get("/") {
        call.respondText("Welcome to Glanci!")
    }
}