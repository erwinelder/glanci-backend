package com.glanci.navigation

import com.glanci.auth.domain.model.UserRole
import com.glanci.mainModule
import com.glanci.navigation.shared.service.NavigationButtonService
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NavigationButtonTest {

    @Test
    fun `test getNavigationButtonsAfterTimestamp`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "navigationButton")
        val categoryService = rcpClient.withService<NavigationButtonService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        val navigationButtons = categoryService.getNavigationButtonsAfterTimestamp(timestamp = 0, token = token)

        assertNotNull(navigationButtons)
        assertEquals(navigationButtons.size, 5)

        client.close()
    }

}