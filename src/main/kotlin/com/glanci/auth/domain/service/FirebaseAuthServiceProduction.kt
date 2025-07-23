package com.glanci.auth.domain.service

import com.glanci.auth.domain.model.firebase.*
import com.glanci.request.domain.error.AuthError
import com.glanci.auth.error.firebase.FirebaseErrorResponse
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.getDataOrReturn
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

class FirebaseAuthServiceProduction : FirebaseAuthService {

    private val firebaseAuthApiUrl = "https://identitytoolkit.googleapis.com/v1/accounts"
    private val firebaseApiKey = System.getenv("FIREBASE_API_KEY")
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { encodeDefaults = true })
        }
    }

    private suspend fun HttpResponse.getJsonObject(): JsonObject {
        return Json.parseToJsonElement(string = this.bodyAsText()).jsonObject
    }

    private suspend fun HttpResponse.getFirebaseAuthResponse(): FirebaseAuthResponse {
        val responseJson = Json.parseToJsonElement(string = this.bodyAsText()).jsonObject

        return FirebaseAuthResponse(
            idToken = responseJson["idToken"]!!.jsonPrimitive.content,
            uid = responseJson["localId"]!!.jsonPrimitive.content,
            email = responseJson["email"]!!.jsonPrimitive.content,
        )
    }

    private suspend fun HttpResponse.getFirebaseUser(idToken: String): FirebaseUser {
        val responseJson = Json.parseToJsonElement(string = this.bodyAsText())
            .jsonObject["users"]!!.jsonArray[0].jsonObject

        return FirebaseUser(
            idToken = idToken,
            uid = responseJson["localId"]!!.jsonPrimitive.content,
            email = responseJson["email"]!!.jsonPrimitive.content,
            emailVerified = responseJson["emailVerified"]!!.jsonPrimitive.boolean,
        )
    }


    private suspend fun lookup(idToken: String): ResultData<FirebaseUser, AuthError> {
        val user = runCatching {
            httpClient.post("$firebaseAuthApiUrl:lookup?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = hashMapOf(
                    "idToken" to idToken
                ))
            }.getFirebaseUser(idToken = idToken)
        }
            .getOrElse { return ResultData.Error(AuthError.ErrorDuringFetchingUserDataFromAuthProvider) }

        return ResultData.Success(data = user)
    }

    private suspend fun applyOobCode(oobCode: String): ResultData<JsonObject, AuthError> {
        val response = httpClient.post("$firebaseAuthApiUrl:update?key=$firebaseApiKey") {
            contentType(ContentType.Application.Json)
            setBody(body = FirebaseApplyOobCodeRequest(oobCode = oobCode))
        }

        val responseJson = response.getJsonObject()
        if (responseJson.containsKey("error")) {
            val error = Json.decodeFromJsonElement<FirebaseErrorResponse>(responseJson)
            return when (error.error.message) {
                "EXPIRED_OOB_CODE" -> ResultData.Error(AuthError.OobCodeExpired)
                "INVALID_OOB_CODE" -> ResultData.Error(AuthError.InvalidOobCode)
                else -> ResultData.Error(AuthError.ErrorDuringVerifyingOobCodeByAuthProvider)
            }
        }
        if (response.status != HttpStatusCode.OK) {
            return ResultData.Error(AuthError.ErrorDuringVerifyingOobCodeByAuthProvider)
        }

        return ResultData.Success(data = responseJson)
    }


    override suspend fun signIn(email: String, password: String): ResultData<FirebaseUser, AuthError> {
        val authResponse = runCatching {
            httpClient.post("$firebaseAuthApiUrl:signInWithPassword?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseCredentialsRequest(email = email, password = password))
            }.getFirebaseAuthResponse()
        }.getOrElse {
            return ResultData.Error(AuthError.InvalidCredentials)
        }

        val user = lookup(idToken = authResponse.idToken).getDataOrReturn { return ResultData.Error(it) }

        if (!user.emailVerified) {
            sendEmailVerification(idToken = user.idToken)
            return ResultData.Error(AuthError.EmailNotVerified)
        }

        return ResultData.Success(data = user)
    }

    override suspend fun signUp(email: String, password: String): ResultData<FirebaseUser, AuthError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:signUp?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseCredentialsRequest(email = email, password = password))
            }
        }.getOrElse {
            return ResultData.Error(AuthError.SignUpFailed)
        }

        val responseJson = Json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        if (responseJson.containsKey("error")) {
            val error = Json.decodeFromJsonElement<FirebaseErrorResponse>(responseJson)
            return when (error.error.message) {
                "EMAIL_EXISTS" -> ResultData.Error(AuthError.UserAlreadyExists)
                else -> ResultData.Error(AuthError.SignUpFailed)
            }
        }

        val authResponse = response.getFirebaseAuthResponse()

        val user = FirebaseUser(
            idToken = authResponse.idToken,
            uid = authResponse.uid,
            email = authResponse.email,
            emailVerified = false
        )

        return ResultData.Success(data = user)
    }

    override suspend fun sendEmailVerification(idToken: String): SimpleResult<AuthError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:sendOobCode?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseVerifyEmailRequest(idToken = idToken))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthError.SendingVerificationEmailFailed)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthError.SendingVerificationEmailFailed)
        }

        return SimpleResult.Success()
    }

    override suspend fun verifyEmail(oobCode: String): ResultData<String, AuthError> {
        val responseJson = runCatching {
            applyOobCode(oobCode = oobCode)
        }
            .getOrElse { return ResultData.Error(AuthError.EmailVerificationFailed) }
            .getDataOrReturn { return ResultData.Error(it) }

        val email = responseJson["email"]?.jsonPrimitive?.content
            ?: return ResultData.Error(AuthError.EmailVerificationFailed)

        return ResultData.Success(data = email)
    }


    override suspend fun requestEmailUpdate(idToken: String, newEmail: String): SimpleResult<AuthError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:sendOobCode?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseVerifyAndChangeEmailRequest(idToken = idToken, newEmail = newEmail))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthError.EmailUpdateRequestFailed)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthError.EmailUpdateRequestFailed)
        }

        return SimpleResult.Success()
    }

    override suspend fun verifyEmailUpdate(oobCode: String): ResultData<String, AuthError> {
        val responseJson = runCatching {
            applyOobCode(oobCode = oobCode)
        }
            .getOrElse { return ResultData.Error(AuthError.EmailUpdateFailed) }
            .getDataOrReturn { return ResultData.Error(it) }

        val email = responseJson["email"]?.jsonPrimitive?.content
            ?: return ResultData.Error(AuthError.EmailUpdateFailed)

        return ResultData.Success(data = email)
    }


    override suspend fun updatePassword(idToken: String, newPassword: String): SimpleResult<AuthError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:update?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseUpdatePasswordRequest(idToken = idToken, password = newPassword))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthError.PasswordUpdateFailed)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthError.PasswordUpdateFailed)
        }

        return SimpleResult.Success()
    }

    override suspend fun requestPasswordReset(email: String): SimpleResult<AuthError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:sendOobCode?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseRequestPasswordResetRequest(email = email))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthError.PasswordResetRequestFailed)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthError.PasswordResetRequestFailed)
        }

        return SimpleResult.Success()
    }

    override suspend fun verifyPasswordReset(oobCode: String, newPassword: String): SimpleResult<AuthError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:resetPassword?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebasePasswordResetRequest(oobCode = oobCode, newPassword = newPassword))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthError.PasswordResetFailed)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthError.PasswordResetFailed)
        }

        return SimpleResult.Success()
    }


    override suspend fun deleteUser(email: String, password: String): SimpleResult<AuthError> {
        val authResponse = runCatching {
            httpClient.post("$firebaseAuthApiUrl:signInWithPassword?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseCredentialsRequest(email = email, password = password))
            }.getFirebaseAuthResponse()
        }.getOrElse {
            return SimpleResult.Error(AuthError.InvalidCredentials)
        }

        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:delete?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = hashMapOf("idToken" to authResponse.idToken))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthError.UserDeletionFailed)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthError.UserDeletionFailed)
        }

        return SimpleResult.Success()
    }

}