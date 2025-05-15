package com.glanci.auth.routes

import com.glanci.auth.domain.dto.EmailUpdateRequestDto
import com.glanci.auth.domain.dto.ResetPasswordRequestDto
import com.glanci.auth.domain.dto.SignUpFormDto
import com.glanci.auth.domain.dto.UpdatePasswordRequestDto
import com.glanci.auth.domain.dto.UserCredentialsDto
import com.glanci.auth.domain.dto.UserWithTokenDto
import com.glanci.auth.domain.model.UserDataValidator
import com.glanci.auth.domain.service.FirebaseAuthService
import com.glanci.auth.domain.service.UserService
import com.glanci.auth.error.AuthError
import com.glanci.auth.mapper.toDomainModel
import com.glanci.auth.mapper.toDto
import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.auth.utils.createJwtToken
import com.glanci.core.utils.receiveOrNull
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.authRoutes(
    firebaseAuthService: FirebaseAuthService,
    userService: UserService
) {
    val secret = System.getenv("JWT_SECRET")?.takeIf { it.isNotBlank() }
        ?: throw AuthError.ErrorDuringExtractingJwtSecret()

    route("/auth") {

        post("/sign-in") {
            val credentials = call.receiveOrNull<UserCredentialsDto>()
                ?: throw AuthError.UserCredentialsAreMissingOrInvalid()

            firebaseAuthService.signIn(email = credentials.email, password = credentials.password)

            val user = userService.getUser(email = credentials.email)
            val token = createJwtToken(user = user, secret = secret)

            call.respond(
                UserWithTokenDto.fromUserAndToken(user = user, token = token)
            )
        }

        post("/sign-up") {
            val form = call.receiveOrNull<SignUpFormDto>()
                ?.toDomainModel()
                ?: throw AuthError.SignUpFormIsMissingOrInvalid()

            if (!UserDataValidator.validateName(form.name)) throw AuthError.InvalidName()
            if (!UserDataValidator.validateEmail(form.email)) throw AuthError.InvalidEmail()
            if (!UserDataValidator.validatePassword(form.password)) throw AuthError.InvalidPassword()

            val user = firebaseAuthService.signUp(email = form.email, password = form.password)
            firebaseAuthService.sendEmailVerification(idToken = user.idToken)

            userService.createUser(email = form.email, name = form.name, language = form.language)

            call.respond(HttpStatusCode.Accepted)
        }

        get("/finish-sign-up/{oobCode}") {
            val oobCode = call.parameters["oobCode"] ?: throw AuthError.OobCodeIsMissing()

            val firebaseUser = firebaseAuthService.verifyEmail(oobCode = oobCode)

            val user = userService.getUser(email = firebaseUser.email)
            val token = createJwtToken(user = user, secret = secret)

            call.respond(
                UserWithTokenDto.fromUserAndToken(user = user, token = token)
            )
        }

        get("request-password-reset/{email}") {
            val email = call.parameters["email"] ?: throw AuthError.EmailIsMissing()

            firebaseAuthService.requestPasswordReset(email = email)

            call.respond(HttpStatusCode.Accepted)
        }

        post("verify-password-reset") {
            val request = call.receiveOrNull<ResetPasswordRequestDto>()
                ?: throw AuthError.PasswordResetRequestIsMissingOrInvalid()

            firebaseAuthService.verifyPasswordReset(oobCode = request.oobCode, newPassword = request.newPassword)

            call.respond(HttpStatusCode.OK)
        }

        authenticate("auth-jwt") {

            post("request-email-update") {
                val userData = authorizeAtLeastAsUser()
                val request = call.receiveOrNull<EmailUpdateRequestDto>()
                    ?: throw AuthError.EmailUpdateRequestIsMissingOrInvalid()

                val user = userService.getUser(id = userData.id)

                val firebaseUser = firebaseAuthService.signIn(email = user.email, password = request.password)
                firebaseAuthService.requestEmailUpdate(idToken = firebaseUser.idToken, newEmail = request.newEmail)

                call.respond(HttpStatusCode.Accepted)
            }

            get("verify-email-update/{oobCode}") {
                val userData = authorizeAtLeastAsUser()
                val oobCode = call.parameters["oobCode"] ?: throw AuthError.OobCodeIsMissing()

                val firebaseUser = firebaseAuthService.verifyEmailUpdate(oobCode = oobCode)

                userService.saveUserEmail(userId = userData.id, email = firebaseUser.email)
                val user = userService.getUser(id = userData.id)
                val token = createJwtToken(user = user, secret = secret)

                call.respond(
                    UserWithTokenDto.fromUserAndToken(user = user, token = token)
                )
            }

            post("update-password") {
                val userData = authorizeAtLeastAsUser()
                val request = call.receiveOrNull<UpdatePasswordRequestDto>()
                    ?: throw AuthError.PasswordUpdateRequestIsMissingOrInvalid()

                val user = userService.getUser(id = userData.id)

                val firebaseUser = firebaseAuthService.signIn(email = user.email, password = request.password)
                firebaseAuthService.updatePassword(idToken = firebaseUser.idToken, newPassword = request.newPassword)

                call.respond(HttpStatusCode.OK)
            }

            get("/check-token-validity") {
                val userData = authorizeAtLeastAsUser()

                val user = userService.getUser(id = userData.id)

                call.respond(user.toDto())
            }

        }

    }
}