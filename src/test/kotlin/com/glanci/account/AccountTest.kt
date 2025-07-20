package com.glanci.account

import com.glanci.account.shared.service.AccountService
import com.glanci.auth.domain.model.UserRole
import com.glanci.mainModule
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AccountTest {

    @Test
    fun `test getAccountsAfterTimestamp`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "account")
        val accountService = rcpClient.withService<AccountService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        val accounts = accountService.getAccountsAfterTimestamp(timestamp = 0, token = token)

        assertNotNull(accounts)
        assertEquals(accounts.size, 2)

        client.close()
    }

}