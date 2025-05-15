package com.glanci.auth.error

import io.ktor.http.*

sealed class AuthError(val statusCode: HttpStatusCode, message: String?) : Throwable(message) {

    class InvalidToken : AuthError(
        statusCode = HttpStatusCode.Unauthorized,
        message = "Token is missing or invalid"
    )

    class InsufficientPermissions : AuthError(
        statusCode = HttpStatusCode.Forbidden,
        message = "Insufficient permissions"
    )

    class UserAuthDataIsMissingOrInvalid : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "User auth data is missing or invalid"
    )

    class ErrorDuringExtractingJwtSecret : AuthError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Error during extracting JWT secret"
    )

    class ErrorDuringCreatingJwtToken : AuthError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Error during creating JWT token"
    )


    class SignUpFormIsMissingOrInvalid : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Sign-up form is missing or invalid"
    )

    class UserCredentialsAreMissingOrInvalid : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "User credentials are missing or invalid"
    )

    class UserIdIsMissing : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "User id is missing"
    )


    class OobCodeIsMissing : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Oob code is missing"
    )

    class OobCodeExpired : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Oob code expired"
    )

    class InvalidOobCode : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Invalid oob code"
    )


    class InvalidCredentials : AuthError(
        statusCode = HttpStatusCode.Unauthorized,
        message = "Invalid credentials"
    )

    class InvalidName : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Invalid name"
    )

    class InvalidEmail : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Invalid email"
    )

    class InvalidPassword : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Invalid password"
    )

    class ErrorDuringFetchingUserDataFromAuthProvider : AuthError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Error during fetching user data from auth provider"
    )

    class UserAlreadyExists : AuthError(
        statusCode = HttpStatusCode.Conflict,
        message = "User already exists"
    )

    class SignUpFailed : AuthError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Sign-up failed"
    )

    class SendingVerificationEmailFailed : AuthError(
        statusCode = HttpStatusCode.ServiceUnavailable,
        message = "Error while sending verification email"
    )

    class EmailVerificationFailed : AuthError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Email verification failed"
    )

    class EmailNotVerified : AuthError(
        statusCode = HttpStatusCode.ExpectationFailed,
        message = "Email not verified"
    )

    class EmailUpdateRequestIsMissingOrInvalid : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Email update request is missing or invalid"
    )

    class EmailUpdateRequestFailed : AuthError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Email update request failed"
    )

    class EmailUpdateFailed : AuthError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Email update failed"
    )

    class PasswordUpdateRequestIsMissingOrInvalid : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Password update request is missing or invalid"
    )

    class PasswordUpdateFailed : AuthError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Password update failed"
    )

    class EmailIsMissing : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Email is missing"
    )

    class PasswordResetRequestFailed : AuthError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Password reset request failed"
    )

    class PasswordResetRequestIsMissingOrInvalid : AuthError(
        statusCode = HttpStatusCode.BadRequest,
        message = "Password reset request is missing or invalid"
    )

    class PasswordResetFailed : AuthError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Password reset failed"
    )

    class DeletingUserFailed : AuthError(
        statusCode = HttpStatusCode.InternalServerError,
        message = "Deleting user failed"
    )

}