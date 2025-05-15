package com.glanci.core.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.glanci.auth.error.AuthError
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val secret = System.getenv("JWT_SECRET") ?: throw AuthError.InvalidToken()
    val issuer = System.getenv("JWT_ISSUER") ?: throw AuthError.InvalidToken()
    val audience = System.getenv("JWT_AUDIENCE") ?: throw AuthError.InvalidToken()
    val myRealm = System.getenv("JWT_REALM") ?: throw AuthError.InvalidToken()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier {
                JWT.require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            }
            validate { credential ->
                credential.payload.getClaim("email").asString()?.let {
                    JWTPrincipal(credential.payload)
                }
            }
            challenge { _, _ ->
                throw AuthError.InvalidToken()
            }
        }
    }
}
