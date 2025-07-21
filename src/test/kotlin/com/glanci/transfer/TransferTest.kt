package com.glanci.transfer

import com.glanci.auth.domain.model.UserRole
import com.glanci.mainModule
import com.glanci.transfer.shared.service.TransferService
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TransferTest {

    @Test
    fun `test getTransfersWithItemsAfterTimestamp`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "transfer")
        val service = rcpClient.withService<TransferService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        val transfers = service.getTransfersAfterTimestamp(timestamp = 0, token = token)

        assertNotNull(transfers)
        assertEquals(transfers.size, 2)

        client.close()
    }

}