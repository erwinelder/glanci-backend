package com.glanci.personalization

import com.glanci.auth.domain.model.UserRole
import com.glanci.mainModule
import com.glanci.personalization.shared.service.WidgetService
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class WidgetTest {

    @Test
    fun `test getWidgetsAfterTimestamp`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "personalization")
        val service = rcpClient.withService<WidgetService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        val widgets = service.getWidgetsAfterTimestamp(timestamp = 0, token = token)

        assertNotNull(widgets)
        assertEquals(widgets.size, 3)

        client.close()
    }

}