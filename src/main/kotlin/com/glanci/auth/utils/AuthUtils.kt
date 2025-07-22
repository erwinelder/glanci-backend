package com.glanci.auth.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.glanci.auth.domain.model.User
import com.glanci.auth.domain.model.UserAuthData
import com.glanci.auth.domain.model.UserRole
import com.glanci.auth.error.AuthError
import com.glanci.auth.error.AuthException
import com.glanci.request.domain.ResultData
import java.util.*


fun createJwtOrNull(user: User): String? {
    val secret = System.getenv("JWT_SECRET")?.takeIf { it.isNotBlank() } ?: return null
    val issuer = System.getenv("JWT_ISSUER")?.takeIf { it.isNotBlank() } ?: return null
    val audience = System.getenv("JWT_AUDIENCE")?.takeIf { it.isNotBlank() } ?: return null

    val algorithm = Algorithm.HMAC256(secret)

    return runCatching {
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("id", user.id.toString())
            .withClaim("role", user.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + 12L * 30 * 24 * 60 * 60 * 1000))
            .sign(algorithm)
    }.getOrNull()
}

@Deprecated("Use verifyAndDecodeJwtResult instead")
fun verifyAndDecodeJwt(token: String): DecodedJWT {
    val secret = System.getenv("JWT_SECRET")?.takeIf { it.isNotBlank() }
        ?: throw AuthException.ErrorDuringExtractingJwtSecret()
    val issuer = System.getenv("JWT_ISSUER") ?: throw AuthException.ErrorDuringExtractingJwtSecret()
    val audience = System.getenv("JWT_AUDIENCE") ?: throw AuthException.ErrorDuringExtractingJwtSecret()
    val algorithm = Algorithm.HMAC256(secret)

    val verifier = JWT.require(algorithm)
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaimPresence("id")
            .withClaimPresence("role")
            .acceptLeeway(0)
            .build()

    return runCatching { verifier.verify(token) }
        .getOrElse { throw AuthException.ErrorDuringExtractingJwtSecret() }
}

fun verifyAndDecodeJwtResult(token: String): ResultData<DecodedJWT, AuthError> {
    val secret = System.getenv("JWT_SECRET")?.takeIf { it.isNotBlank() }
    val issuer = System.getenv("JWT_ISSUER")?.takeIf { it.isNotBlank() }
    val audience = System.getenv("JWT_AUDIENCE")?.takeIf { it.isNotBlank() }

    if (secret == null || issuer == null || audience == null) {
        return ResultData.Error(AuthError.ErrorDuringExtractingJwtSecret)
    }

    val algorithm = Algorithm.HMAC256(secret)

    val jwt = runCatching {
        JWT.require(algorithm)
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaimPresence("id")
            .withClaimPresence("role")
            .acceptLeeway(0)
            .build()
            .verify(token)
    }
        .getOrElse { return ResultData.Error(AuthError.InvalidToken) }

    return ResultData.Success(data = jwt)
}


fun DecodedJWT.getClaimFromJwt(claim: String): String {
    return getClaim(claim).asString()
}

fun DecodedJWT.getIdFromJwt(): Int {
    return getClaimFromJwt("id").toInt()
}

fun DecodedJWT.getRoleFromJwt(): UserRole {
    return enumValueOf<UserRole>(name = getClaimFromJwt("role"))
}


@Deprecated("Use authorizeAtLeastAsUserResult instead")
fun authorizeAtLeastAsUser(token: String): UserAuthData {
    val jwt = verifyAndDecodeJwt(token = token)

    return UserAuthData(id = jwt.getIdFromJwt(), role = jwt.getRoleFromJwt())
}

fun authorizeAtLeastAsUserResult(token: String): ResultData<UserAuthData, AuthError> {
    return verifyAndDecodeJwtResult(token = token).mapData {
        UserAuthData(id = it.getIdFromJwt(), role = it.getRoleFromJwt())
    }
}

fun authorizeAsAdmin(token: String): ResultData<UserAuthData, AuthError> {
    val jwtResult = verifyAndDecodeJwtResult(token = token)

    return when (jwtResult) {
        is ResultData.Success -> {
            val jwt = jwtResult.data

            val role = jwt.getRoleFromJwt()
            if (role != UserRole.Admin) {
                ResultData.Error(AuthError.InsufficientPermissions)
            } else {
                ResultData.Success(data = UserAuthData(id = jwt.getIdFromJwt(), role = UserRole.Admin))
            }
        }
        is ResultData.Error -> {
            ResultData.Error(jwtResult.error)
        }
    }
}
