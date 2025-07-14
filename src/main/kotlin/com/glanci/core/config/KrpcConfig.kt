package com.glanci.core.config

import io.ktor.client.request.HttpRequestBuilder
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.ktor.server.KrpcRoute
import kotlinx.rpc.krpc.serialization.json.json

fun KrpcRoute.configureKrpc() {
    rpcConfig {
        waitForServices = false
        serialization {
            json()
        }
    }
}

fun HttpRequestBuilder.configureKrpc() {
    rpcConfig {
        serialization {
            json()
        }
    }
}