package com.glanci.core.config

import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI(vararg modules: Module) {
    install(Koin) {
        slf4jLogger()
        modules(
            modules = modules
        )
    }
}
