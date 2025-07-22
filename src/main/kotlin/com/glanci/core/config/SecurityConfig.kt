package com.glanci.core.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.glanci.auth.error.AuthException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val secret = System.getenv("JWT_SECRET") ?: throw AuthException.ErrorDuringExtractingJwtSecret()
    val issuer = System.getenv("JWT_ISSUER") ?: throw AuthException.ErrorDuringExtractingJwtSecret()
    val audience = System.getenv("JWT_AUDIENCE") ?: throw AuthException.ErrorDuringExtractingJwtSecret()
    val myRealm = System.getenv("JWT_REALM") ?: throw AuthException.ErrorDuringExtractingJwtSecret()

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
                credential.payload.getClaim("id").asString()?.let {
                    JWTPrincipal(credential.payload)
                }
            }
            validate { credential ->
                credential.payload.getClaim("role").asString()?.let {
                    JWTPrincipal(credential.payload)
                }
            }
            challenge { _, _ ->
                throw AuthException.InvalidToken()
            }
        }
    }
}
