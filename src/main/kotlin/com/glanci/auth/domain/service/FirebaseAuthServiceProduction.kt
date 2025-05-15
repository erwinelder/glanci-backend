package com.glanci.auth.domain.service

import com.glanci.auth.domain.model.firebase.*
import com.glanci.auth.error.AuthError
import com.glanci.auth.error.firebase.FirebaseErrorResponse
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
            json()
        }
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


    private suspend fun lookup(idToken: String): FirebaseUser {
        return try {
            httpClient.post("$firebaseAuthApiUrl:lookup?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = hashMapOf(
                    "idToken" to idToken
                ))
            }.getFirebaseUser(idToken = idToken)
        } catch (_: Exception) {
            throw AuthError.ErrorDuringFetchingUserDataFromAuthProvider()
        }
    }

    private suspend fun applyOobCode(oobCode: String): FirebaseAuthResponse {
        val response = httpClient.post("$firebaseAuthApiUrl:update?key=$firebaseApiKey") {
            contentType(ContentType.Application.Json)
            setBody(body = FirebaseApplyOobCodeRequest(oobCode = oobCode))
        }

        val responseJson = Json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        if (responseJson.containsKey("error")) {
            val error = Json.decodeFromJsonElement<FirebaseErrorResponse>(responseJson)
            when (error.error.message) {
                "EXPIRED_OOB_CODE" -> throw AuthError.OobCodeExpired()
                "INVALID_OOB_CODE" -> throw AuthError.InvalidOobCode()
                else -> throw Exception()
            }
        }
        if (response.status != HttpStatusCode.OK) {
            throw Exception()
        }

        return response.getFirebaseAuthResponse()
    }


    override suspend fun signIn(email: String, password: String): FirebaseUser {
        val authResponse = try {
            httpClient.post("$firebaseAuthApiUrl:signInWithPassword?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseCredentialsRequest(email = email, password = password))
            }.getFirebaseAuthResponse()
        } catch (_: Exception) {
            throw AuthError.InvalidCredentials()
        }

        val user = lookup(idToken = authResponse.idToken)

        if (!user.emailVerified) throw AuthError.EmailNotVerified()

        return user
    }

    override suspend fun signUp(email: String, password: String): FirebaseUser {
        val response = try {
            httpClient.post("$firebaseAuthApiUrl:signUp?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseCredentialsRequest(email = email, password = password))
            }
        } catch (_: Exception) {
            throw AuthError.SignUpFailed()
        }

        val responseJson = Json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        if (responseJson.containsKey("error")) {
            val error = Json.decodeFromJsonElement<FirebaseErrorResponse>(responseJson)
            when (error.error.message) {
                "EMAIL_EXISTS" -> throw AuthError.UserAlreadyExists()
                else -> throw AuthError.SignUpFailed()
            }
        }

        val authResponse = response.getFirebaseAuthResponse()

        return FirebaseUser(
            idToken = authResponse.idToken,
            uid = authResponse.uid,
            email = authResponse.email,
            emailVerified = false
        )
    }

    override suspend fun sendEmailVerification(idToken: String) {
        val response = try {
            httpClient.post("$firebaseAuthApiUrl:sendOobCode?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseVerifyEmailRequest(idToken = idToken))
            }
        } catch (_: Exception) {
            throw AuthError.SendingVerificationEmailFailed()
        }

        if (response.status != HttpStatusCode.OK) {
            throw AuthError.SendingVerificationEmailFailed()
        }
    }

    override suspend fun verifyEmail(oobCode: String): FirebaseUser {
        val authResponse = try {
            applyOobCode(oobCode = oobCode)
        } catch (e: AuthError) {
            throw e
        } catch (_: Exception) {
            throw AuthError.EmailVerificationFailed()
        }

        return lookup(idToken = authResponse.idToken)
    }


    override suspend fun requestEmailUpdate(idToken: String, newEmail: String) {
        val response = try {
            httpClient.post("$firebaseAuthApiUrl:sendOobCode?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseVerifyAndChangeEmailRequest(idToken = idToken, email = newEmail))
            }
        } catch (_: Exception) {
            throw AuthError.EmailUpdateRequestFailed()
        }

        if (response.status != HttpStatusCode.OK) {
            throw AuthError.EmailUpdateRequestFailed()
        }
    }

    override suspend fun verifyEmailUpdate(oobCode: String): FirebaseUser {
        val authResponse = try {
            applyOobCode(oobCode = oobCode)
        } catch (e: AuthError) {
            throw e
        } catch (_: Exception) {
            throw AuthError.EmailUpdateFailed()
        }

        return lookup(idToken = authResponse.idToken)
    }


    override suspend fun updatePassword(idToken: String, newPassword: String) {
        val response = try {
            httpClient.post("$firebaseAuthApiUrl:update?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseUpdatePasswordRequest(idToken = idToken, password = newPassword))
            }
        } catch (_: Exception) {
            throw AuthError.PasswordUpdateFailed()
        }

        if (response.status != HttpStatusCode.OK) {
            throw AuthError.PasswordUpdateFailed()
        }
    }

    override suspend fun requestPasswordReset(email: String) {
        val response = try {
            httpClient.post("$firebaseAuthApiUrl:sendOobCode?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebaseRequestPasswordResetRequest(email = email))
            }
        } catch (_: Exception) {
            throw AuthError.PasswordResetRequestFailed()
        }

        if (response.status != HttpStatusCode.OK) {
            throw AuthError.PasswordResetRequestFailed()
        }
    }

    override suspend fun verifyPasswordReset(oobCode: String, newPassword: String) {
        val response = try {
            httpClient.post("$firebaseAuthApiUrl:resetPassword?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = FirebasePasswordResetRequest(oobCode = oobCode, newPassword = newPassword))
            }
        } catch (_: Exception) {
            throw AuthError.PasswordResetFailed()
        }

        if (response.status != HttpStatusCode.OK) {
            throw AuthError.PasswordResetFailed()
        }
    }


    override suspend fun deleteUser(email: String, password: String) {
        val idToken = signIn(email = email, password = password).idToken

        try {
            val response = httpClient.post("$firebaseAuthApiUrl:delete?key=$firebaseApiKey") {
                contentType(ContentType.Application.Json)
                setBody(body = hashMapOf("idToken" to idToken))
            }

            if (response.status != HttpStatusCode.OK) {
                throw AuthError.DeletingUserFailed()
            }
        } catch (_: Exception) {
            throw AuthError.DeletingUserFailed()
        }
    }

}