package com.glanci.navigation

import com.glanci.auth.domain.model.UserRole
import com.glanci.core.utils.getCurrentTimestamp
import com.glanci.mainModule
import com.glanci.navigation.shared.dto.NavigationButtonDto
import com.glanci.navigation.shared.service.NavigationButtonService
import com.glanci.request.domain.error.AuthError
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NavigationButtonTest {

    @Test
    fun `getNavigationButtonsAfterTimestamp returns navigation buttons`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "navigation").withService<NavigationButtonService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getNavigationButtonsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(5, this.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `saveNavigationButtons returns success`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "navigation").withService<NavigationButtonService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getNavigationButtonsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(5, this.size)
        }

        val timestamp = getCurrentTimestamp()
        val buttonsToSave = listOf(
            NavigationButtonDto(screenName = "New screen", orderNum = 8143, timestamp = 1644, deleted = false)
        )

        service.saveNavigationButtons(buttons = buttonsToSave, timestamp = timestamp, token = token).getErrorOrNull().run {
            assertNull(this)
        }

        service.getNavigationButtonsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(6, this.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(timestamp, this)
        }

        client.close()
    }

    @Test
    fun `getNavigationButtonsAfterTimestamp returns AuthError InvalidToken`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "navigation").withService<NavigationButtonService>()

        service.getNavigationButtonsAfterTimestamp(timestamp = 0, token = "token").getErrorOrNull().run {
            assertNotNull(this)
            assertEquals(AuthError.InvalidToken, this)
        }

        client.close()
    }

}