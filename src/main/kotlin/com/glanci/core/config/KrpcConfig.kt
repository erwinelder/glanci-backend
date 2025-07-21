package com.glanci.core.config

import io.ktor.client.request.*
import io.ktor.server.application.*
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.serialization.protobuf.protobuf
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureKrpc() {
    install(Krpc) {
        serialization {
            protobuf()
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun HttpRequestBuilder.configureKrpc() {
    rpcConfig {
        serialization {
            protobuf()
        }
    }
}