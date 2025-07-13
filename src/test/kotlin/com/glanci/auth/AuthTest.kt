package com.glanci.auth

import com.glanci.auth.domain.dto.UserCredentialsDto
import com.glanci.auth.domain.dto.UserWithTokenDto
import com.glanci.mainModule
import com.glanci.utils.getClient
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthTest {

    @Test
    fun `test signIn`() = testApplication {
        application { mainModule() }
        client = getClient()

        val response = client.post("/auth/sign-in") {
            contentType(ContentType.Application.Json)
            setBody(
                UserCredentialsDto(email = "base_user@domain.com", password = "password")
            )
        }
        val userWithToken = Json.decodeFromString<UserWithTokenDto>(string = response.bodyAsText())

        assertEquals(userWithToken.email, "base_user@domain.com")
    }

}