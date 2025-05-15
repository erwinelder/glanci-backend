package com.glanci.auth.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.glanci.auth.domain.model.User
import com.glanci.auth.domain.model.UserAuthData
import com.glanci.auth.domain.model.UserRole
import com.glanci.auth.error.AuthError
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import java.util.*


fun createJwtToken(user: User, secret: String): String {
    val issuer = System.getenv("JWT_ISSUER") ?: throw AuthError.ErrorDuringExtractingJwtSecret()
    val audience = System.getenv("JWT_AUDIENCE") ?: throw AuthError.ErrorDuringExtractingJwtSecret()

    return try {
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("id", user.id.toString())
            .withClaim("role", user.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + 12L * 30 * 24 * 3600 * 1000))
            .sign(Algorithm.HMAC256(secret))
    } catch (_: Exception) {
        throw AuthError.ErrorDuringCreatingJwtToken()
    }
}


fun RoutingContext.verifyJwtToken(): JWTPrincipal {
    return call.principal<JWTPrincipal>() ?: throw AuthError.InvalidToken()
}

fun JWTPrincipal.getClaim(claim: String): String {
    return payload.getClaim(claim).asString()
}

fun JWTPrincipal.getId(): Int {
    return getClaim("id").toInt()
}

fun JWTPrincipal.getRole(): UserRole {
    return enumValueOf<UserRole>(name = getClaim("role"))
}

fun JWTPrincipal.getTokenExpirationTimeInMinutes(): Long? {
    return expiresAt?.time?.minus(System.currentTimeMillis())?.div(60000)
}

fun JWTPrincipal.getTokenExpirationTimeInMinutesOrZero(): Long {
    return expiresAt?.time?.minus(System.currentTimeMillis())?.div(60000) ?: 0
}

fun RoutingContext.authorizeAtLeastAsUser(): UserAuthData {
    val principal = verifyJwtToken()
    val role = principal.getRole()

    if (role !in listOf(UserRole.User, UserRole.Admin)) {
        throw AuthError.InsufficientPermissions()
    }

    return UserAuthData(id = principal.getId(), role = role)
}

fun RoutingContext.authorizeAsAdmin(): UserAuthData {
    val principal = verifyJwtToken()
    val role = principal.getRole()

    if (role != UserRole.Admin) throw AuthError.InsufficientPermissions()

    return UserAuthData(id = principal.getId(), role = role)
}