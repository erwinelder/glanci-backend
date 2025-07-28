package com.glanci.auth

import com.glanci.request.shared.error.AuthDataError
import com.glanci.auth.shared.service.AuthService
import com.glanci.mainModule
import com.glanci.utils.configureRcp
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthTest {

    @Test
    fun `signIn returns success`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "auth")
        val service = rcpClient.withService<AuthService>()

        val userWithToken = service.signIn(email = "base_user@domain.com", password = "password").getDataOrNull()

        assertNotNull(userWithToken)
        assertEquals("base_user@domain.com", userWithToken.email)

        client.close()
    }

    @Test
    fun `successful signIn via Firebase creates a new user in the database`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "auth")
        val service = rcpClient.withService<AuthService>()

        val userWithToken = service.signIn(email = "new_user@domain.com", password = "password").getDataOrNull()

        assertNotNull(userWithToken)
        assertEquals("new_user@domain.com", userWithToken.email)

        client.close()
    }

    @Test
    fun `signUp returns success`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "auth")
        val service = rcpClient.withService<AuthService>()

        val signInError = service.signIn(email = "not_existing_user@domain.com", password = "password").getErrorOrNull()

        assertEquals(AuthDataError.InvalidCredentials, signInError)

        val signUpError = service.signUp(
            name = "New user",
            email = "not_existing_user@domain.com",
            password = "Password0_",
            langCode = "en"
        ).getErrorOrNull()

        assertNull(signUpError)

        val userWithToken = service.signIn(email = "not_existing_user@domain.com", password = "password").getDataOrNull()

        assertNotNull(userWithToken)
        assertEquals(4, userWithToken.id)
        assertEquals("not_existing_user@domain.com", userWithToken.email)

        client.close()
    }

}