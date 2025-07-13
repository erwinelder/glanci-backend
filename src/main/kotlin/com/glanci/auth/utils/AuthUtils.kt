package com.glanci.auth.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.glanci.auth.domain.model.User
import com.glanci.auth.domain.model.UserAuthData
import com.glanci.auth.domain.model.UserRole
import com.glanci.auth.error.AuthError
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import java.util.*


fun createJwt(user: User): String {
    val secret = System.getenv("JWT_SECRET")?.takeIf { it.isNotBlank() }
        ?: throw AuthError.ErrorDuringExtractingJwtSecret()
    val issuer = System.getenv("JWT_ISSUER") ?: throw AuthError.ErrorDuringExtractingJwtSecret()
    val audience = System.getenv("JWT_AUDIENCE") ?: throw AuthError.ErrorDuringExtractingJwtSecret()
    val algorithm = Algorithm.HMAC256(secret)

    return try {
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("id", user.id.toString())
            .withClaim("role", user.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + 12L * 30 * 24 * 60 * 60 * 1000))
            .sign(algorithm)
    } catch (_: Exception) {
        throw AuthError.ErrorDuringCreatingJwtToken()
    }
}

fun verifyAndDecodeJwt(token: String): DecodedJWT {
    val secret = System.getenv("JWT_SECRET")?.takeIf { it.isNotBlank() }
        ?: throw AuthError.ErrorDuringExtractingJwtSecret()
    val issuer = System.getenv("JWT_ISSUER") ?: throw AuthError.ErrorDuringExtractingJwtSecret()
    val audience = System.getenv("JWT_AUDIENCE") ?: throw AuthError.ErrorDuringExtractingJwtSecret()
    val algorithm = Algorithm.HMAC256(secret)

    val verifier = JWT.require(algorithm)
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaimPresence("id")
            .withClaimPresence("role")
            .acceptLeeway(0)
            .build()

    return runCatching { verifier.verify(token) }
        .getOrElse { throw AuthError.ErrorDuringExtractingJwtSecret() }
}


fun RoutingContext.verifyJwtToken(): JWTPrincipal {
    return call.principal<JWTPrincipal>() ?: throw AuthError.InvalidToken()
}

fun JWTPrincipal.getClaimFromPrincipal(claim: String): String {
    return payload.getClaim(claim).asString()
}

fun DecodedJWT.getClaimFromJwt(claim: String): String {
    return getClaim(claim).asString()
}

fun JWTPrincipal.getIdFromPrincipal(): Int {
    return getClaimFromPrincipal("id").toInt()
}

fun DecodedJWT.getIdFromJwt(): Int {
    return getClaimFromJwt("id").toInt()
}

fun JWTPrincipal.getRoleFromPrincipal(): UserRole {
    return enumValueOf<UserRole>(name = getClaimFromPrincipal("role"))
}

fun DecodedJWT.getRoleFromJwt(): UserRole {
    return enumValueOf<UserRole>(name = getClaimFromJwt("role"))
}

fun JWTPrincipal.getTokenExpirationTimeInMinutesFromPrincipal(): Long? {
    return expiresAt?.time?.minus(System.currentTimeMillis())?.div(60000)
}

fun JWTPrincipal.getTokenExpirationTimeInMinutesOrZeroFromPrincipal(): Long {
    return expiresAt?.time?.minus(System.currentTimeMillis())?.div(60000) ?: 0
}


fun RoutingContext.authorizeAtLeastAsUser(): UserAuthData {
    val principal = verifyJwtToken()
    val role = principal.getRoleFromPrincipal()

    if (role !in listOf(UserRole.User, UserRole.Admin)) {
        throw AuthError.InsufficientPermissions()
    }

    return UserAuthData(id = principal.getIdFromPrincipal(), role = role)
}

fun authorizeAtLeastAsUser(token: String): UserAuthData {
    val jwt = verifyAndDecodeJwt(token = token)

    return UserAuthData(id = jwt.getIdFromJwt(), role = jwt.getRoleFromJwt())
}

fun RoutingContext.authorizeAsAdmin(): UserAuthData {
    val principal = verifyJwtToken()
    val role = principal.getRoleFromPrincipal()

    if (role != UserRole.Admin) throw AuthError.InsufficientPermissions()

    return UserAuthData(id = principal.getIdFromPrincipal(), role = role)
}

fun authorizeAsAdmin(token: String): UserAuthData {
    val jwt = verifyAndDecodeJwt(token = token)
    val role = jwt.getRoleFromJwt()

    if (role != UserRole.Admin) throw AuthError.InsufficientPermissions()

    return UserAuthData(id = jwt.getIdFromJwt(), role = UserRole.Admin)
}
