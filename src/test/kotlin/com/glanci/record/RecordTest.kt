package com.glanci.record

import com.glanci.auth.domain.model.UserRole
import com.glanci.core.utils.getCurrentTimestamp
import com.glanci.mainModule
import com.glanci.record.shared.dto.RecordCommandDto
import com.glanci.record.shared.dto.RecordItemDto
import com.glanci.record.shared.dto.RecordWithItemsCommandDto
import com.glanci.record.shared.service.RecordService
import com.glanci.request.shared.error.AuthDataError
import com.glanci.request.shared.error.RecordDataError
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RecordTest {

    @Test
    fun `getRecordsWithItemsAfterTimestamp returns records with items`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "record").withService<RecordService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getRecordsWithItemsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val items = this?.flatMap { it.items }

            assertNotNull(this)
            assertEquals(2, this.size)
            assertNotNull(items)
            assertEquals(3, items.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `saveAccounts returns success`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "record").withService<RecordService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getRecordsWithItemsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val items = this?.flatMap { it.items }

            assertNotNull(this)
            assertEquals(2, this.size)
            assertNotNull(items)
            assertEquals(3, items.size)
        }

        val timestamp = getCurrentTimestamp()
        val recordsWithItemsToSave = listOf(
            RecordWithItemsCommandDto(
                record = RecordCommandDto(
                    id = 4960,
                    date = 1739,
                    type = '-',
                    accountId = 1,
                    includeInBudgets = false,
                    timestamp = timestamp,
                    deleted = false
                ),
                items = listOf(
                    RecordItemDto(
                        id = 3084,
                        recordId = 4960,
                        totalAmount = 6.7,
                        quantity = 2382,
                        categoryId = 2265,
                        subcategoryId = 7247,
                        note = "malorum"
                    )
                )
            )
        )

        service.saveRecordsWithItems(
            recordsWithItems = recordsWithItemsToSave, timestamp = timestamp, token = token
        ).getErrorOrNull().run {
            assertNull(this)
        }

        service.getRecordsWithItemsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val items = this?.flatMap { it.items }

            assertNotNull(this)
            assertEquals(3, this.size)
            assertNotNull(items)
            assertEquals(4, items.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(timestamp, this)
        }

        client.close()
    }

    @Test
    fun `saveAccounts returns RecordError RecordsWithItemsNotSaved`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "record").withService<RecordService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getRecordsWithItemsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val items = this?.flatMap { it.items }

            assertNotNull(this)
            assertEquals(2, this.size)
            assertNotNull(items)
            assertEquals(3, items.size)
        }

        val timestamp = getCurrentTimestamp()
        val recordsWithItemsToSave = listOf(
            RecordWithItemsCommandDto(
                record = RecordCommandDto(
                    id = 4960,
                    date = 1739,
                    type = '-',
                    accountId = 100,
                    includeInBudgets = false,
                    timestamp = timestamp,
                    deleted = false
                ),
                items = listOf(
                    RecordItemDto(
                        id = 3084,
                        recordId = 4960,
                        totalAmount = 6.7,
                        quantity = 2382,
                        categoryId = 2265,
                        subcategoryId = 7247,
                        note = "malorum"
                    )
                )
            )
        )

        service.saveRecordsWithItems(
            recordsWithItems = recordsWithItemsToSave, timestamp = timestamp, token = token
        ).getErrorOrNull().run {
            assertEquals(RecordDataError.RecordsWithItemsNotSaved, this)
        }

        service.getRecordsWithItemsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val items = this?.flatMap { it.items }

            assertNotNull(this)
            assertEquals(2, this.size)
            assertNotNull(items)
            assertEquals(3, items.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `getRecordsWithItemsAfterTimestamp returns AuthError InvalidToken`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "record").withService<RecordService>()

        service.getRecordsWithItemsAfterTimestamp(timestamp = 0, token = "token").getErrorOrNull().run {
            assertNotNull(this)
            assertEquals(AuthDataError.InvalidToken, this)
        }

        client.close()
    }

}