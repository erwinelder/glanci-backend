package com.glanci.record

import com.glanci.auth.domain.model.UserRole
import com.glanci.mainModule
import com.glanci.record.shared.service.RecordService
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RecordTest {

    @Test
    fun `test getRecordsWithItemsAfterTimestamp`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "record")
        val service = rcpClient.withService<RecordService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        val recordsWithItems = service.getRecordsWithItemsAfterTimestamp(timestamp = 0, token = token)
        val items = recordsWithItems?.flatMap { it.items }

        assertNotNull(recordsWithItems)
        assertEquals(recordsWithItems.size, 2)
        assertNotNull(items)
        assertEquals(items.size, 3)

        client.close()
    }

}