package com.glanci.auth.domain.service

import com.glanci.auth.domain.model.firebase.*
import com.glanci.request.shared.error.AuthDataError
import com.glanci.auth.error.firebase.FirebaseErrorResponse
import com.glanci.request.shared.ResultData
import com.glanci.request.shared.SimpleResult
import com.glanci.request.shared.getDataOrReturn
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


    private suspend fun lookup(idToken: String): ResultData<FirebaseUser, AuthDataError> {
        val user = runCatching {
            httpClient.post("$firebaseAuthApiUrl:lookup?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = hashMapOf(
                    "idToken" to idToken
                ))
            }.getFirebaseUser(idToken = idToken)
        }
            .getOrElse { return ResultData.Error(AuthDataError.ErrorDuringFetchingUserDataFromAuthProvider) }

        return ResultData.Success(data = user)
    }

    private suspend fun applyOobCode(oobCode: String): ResultData<JsonObject, AuthDataError> {
        val response = httpClient.post("$firebaseAuthApiUrl:update?key=$firebaseApiKey") {
            contentType(ContentType.Application.Json)
            setBody(body = FirebaseApplyOobCodeRequest(oobCode = oobCode))
        }

        val responseJson = response.getJsonObject()
        if (responseJson.containsKey("error")) {
            val error = Json.decodeFromJsonElement<FirebaseErrorResponse>(responseJson)
            return when (error.error.message) {
                "EXPIRED_OOB_CODE" -> ResultData.Error(AuthDataError.OobCodeExpired)
                "INVALID_OOB_CODE" -> ResultData.Error(AuthDataError.InvalidOobCode)
                else -> ResultData.Error(AuthDataError.ErrorDuringVerifyingOobCodeAtAuthProvider)
            }
        }
        if (response.status != HttpStatusCode.OK) {
            return ResultData.Error(AuthDataError.ErrorDuringVerifyingOobCodeAtAuthProvider)
        }

        return ResultData.Success(data = responseJson)
    }


    override suspend fun signIn(email: String, password: String): ResultData<FirebaseUser, AuthDataError> {
        val authResponse = runCatching {
            httpClient.post("$firebaseAuthApiUrl:signInWithPassword?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseCredentialsRequest(email = email, password = password))
            }.getFirebaseAuthResponse()
        }.getOrElse {
            return ResultData.Error(AuthDataError.InvalidCredentials)
        }

        val user = lookup(idToken = authResponse.idToken).getDataOrReturn { return ResultData.Error(it) }

        if (!user.emailVerified) {
            sendEmailVerification(idToken = user.idToken)
            return ResultData.Error(AuthDataError.EmailNotVerified)
        }

        return ResultData.Success(data = user)
    }

    override suspend fun signUp(email: String, password: String): ResultData<FirebaseUser, AuthDataError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:signUp?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseCredentialsRequest(email = email, password = password))
            }
        }.getOrElse {
            return ResultData.Error(AuthDataError.SignUpFailed)
        }

        val responseJson = Json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        if (responseJson.containsKey("error")) {
            val error = Json.decodeFromJsonElement<FirebaseErrorResponse>(responseJson)
            return when (error.error.message) {
                "EMAIL_EXISTS" -> ResultData.Error(AuthDataError.UserAlreadyExists)
                else -> ResultData.Error(AuthDataError.SignUpFailed)
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

    override suspend fun sendEmailVerification(idToken: String): SimpleResult<AuthDataError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:sendOobCode?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseVerifyEmailRequest(idToken = idToken))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthDataError.SendingVerificationEmailFailed)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthDataError.SendingVerificationEmailFailed)
        }

        return SimpleResult.Success()
    }

    override suspend fun verifyEmail(oobCode: String): ResultData<String, AuthDataError> {
        val responseJson = runCatching {
            applyOobCode(oobCode = oobCode)
        }
            .getOrElse { return ResultData.Error(AuthDataError.EmailVerificationFailed) }
            .getDataOrReturn { return ResultData.Error(it) }

        val email = responseJson["email"]?.jsonPrimitive?.content
            ?: return ResultData.Error(AuthDataError.EmailVerificationFailed)

        return ResultData.Success(data = email)
    }


    override suspend fun requestEmailUpdate(idToken: String, newEmail: String): SimpleResult<AuthDataError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:sendOobCode?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseVerifyAndChangeEmailRequest(idToken = idToken, newEmail = newEmail))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthDataError.EmailUpdateRequestFailed)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthDataError.EmailUpdateRequestFailed)
        }

        return SimpleResult.Success()
    }

    override suspend fun verifyEmailUpdate(oobCode: String): ResultData<String, AuthDataError> {
        val responseJson = runCatching {
            applyOobCode(oobCode = oobCode)
        }
            .getOrElse { return ResultData.Error(AuthDataError.EmailUpdateFailed) }
            .getDataOrReturn { return ResultData.Error(it) }

        val email = responseJson["email"]?.jsonPrimitive?.content
            ?: return ResultData.Error(AuthDataError.EmailUpdateFailed)

        return ResultData.Success(data = email)
    }


    override suspend fun updatePassword(idToken: String, newPassword: String): SimpleResult<AuthDataError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:update?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseUpdatePasswordRequest(idToken = idToken, password = newPassword))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthDataError.PasswordUpdateFailed)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthDataError.PasswordUpdateFailed)
        }

        return SimpleResult.Success()
    }

    override suspend fun requestPasswordReset(email: String): SimpleResult<AuthDataError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:sendOobCode?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseRequestPasswordResetRequest(email = email))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthDataError.PasswordResetRequestFailed)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthDataError.PasswordResetRequestFailed)
        }

        return SimpleResult.Success()
    }

    override suspend fun verifyPasswordReset(oobCode: String, newPassword: String): SimpleResult<AuthDataError> {
        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:resetPassword?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebasePasswordResetRequest(oobCode = oobCode, newPassword = newPassword))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthDataError.PasswordResetFailed)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthDataError.PasswordResetFailed)
        }

        return SimpleResult.Success()
    }


    override suspend fun deleteUser(email: String, password: String): SimpleResult<AuthDataError> {
        val authResponse = runCatching {
            httpClient.post("$firebaseAuthApiUrl:signInWithPassword?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseCredentialsRequest(email = email, password = password))
            }.getFirebaseAuthResponse()
        }.getOrElse {
            return SimpleResult.Error(AuthDataError.InvalidCredentials)
        }

        val response = runCatching {
            httpClient.post("$firebaseAuthApiUrl:delete?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = hashMapOf("idToken" to authResponse.idToken))
            }
        }.getOrElse {
            return SimpleResult.Error(AuthDataError.ErrorDuringDeletingUserAtAuthProvider)
        }

        if (response.status != HttpStatusCode.OK) {
            return SimpleResult.Error(AuthDataError.ErrorDuringDeletingUserAtAuthProvider)
        }

        return SimpleResult.Success()
    }

}