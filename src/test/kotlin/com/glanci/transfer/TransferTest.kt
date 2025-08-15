package com.glanci.transfer

import com.glanci.auth.shared.dto.UserRole
import com.glanci.core.utils.getCurrentTimestamp
import com.glanci.mainModule
import com.glanci.request.shared.error.AuthDataError
import com.glanci.request.shared.error.TransferDataError
import com.glanci.transfer.shared.dto.TransferCommandDto
import com.glanci.transfer.shared.service.TransferService
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TransferTest {

    @Test
    fun `getTransfersWithItemsAfterTimestamp returns transfers`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "transfer").withService<TransferService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getTransfersAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(2, this.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `saveTransfers returns success`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "transfer").withService<TransferService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getTransfersAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(2, this.size)
        }

        val timestamp = getCurrentTimestamp()
        val transfersToSave = listOf(
            TransferCommandDto(
                id = 7083,
                date = 2625,
                senderAccountId = 1,
                receiverAccountId = 2,
                senderAmount = 16.17,
                receiverAmount = 18.19,
                senderRate = 20.21,
                receiverRate = 22.23,
                includeInBudgets = false,
                timestamp = timestamp,
                deleted = false
            ),
            TransferCommandDto(
                id = 7084,
                date = 2626,
                senderAccountId = 2,
                receiverAccountId = 1,
                senderAmount = 26.27,
                receiverAmount = 28.29,
                senderRate = 30.31,
                receiverRate = 32.33,
                includeInBudgets = false,
                timestamp = timestamp,
                deleted = false
            )
        )

        service.saveTransfers(transfers = transfersToSave, timestamp = timestamp, token = token).getErrorOrNull().run {
            assertNull(this)
        }

        service.getTransfersAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(4, this.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(timestamp, this)
        }

        client.close()
    }

    @Test
    fun `saveTransfers returns TransferError TransfersNotSaved`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "transfer").withService<TransferService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getTransfersAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(2, this.size)
        }

        val timestamp = getCurrentTimestamp()
        val transfersToSave = listOf(
            TransferCommandDto(
                id = 7083,
                date = 2625,
                senderAccountId = 100,
                receiverAccountId = 200,
                senderAmount = 16.17,
                receiverAmount = 18.19,
                senderRate = 20.21,
                receiverRate = 22.23,
                includeInBudgets = false,
                timestamp = timestamp,
                deleted = false
            ),
            TransferCommandDto(
                id = 7084,
                date = 2626,
                senderAccountId = 200,
                receiverAccountId = 100,
                senderAmount = 26.27,
                receiverAmount = 28.29,
                senderRate = 30.31,
                receiverRate = 32.33,
                includeInBudgets = false,
                timestamp = timestamp,
                deleted = false
            )
        )

        service.saveTransfers(transfers = transfersToSave, timestamp = timestamp, token = token).getErrorOrNull().run {
            assertEquals(TransferDataError.TransfersNotSaved, this)
        }

        service.getTransfersAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(2, this.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `getTransfersAfterTimestamp returns AuthError InvalidToken`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "transfer").withService<TransferService>()

        service.getTransfersAfterTimestamp(timestamp = 0, token = "token").getErrorOrNull().run {
            assertNotNull(this)
            assertEquals(AuthDataError.InvalidToken, this)
        }

        client.close()
    }

}