package com.glanci.account

import com.glanci.account.shared.dto.AccountCommandDto
import com.glanci.account.shared.service.AccountService
import com.glanci.auth.domain.model.UserRole
import com.glanci.core.utils.getCurrentTimestamp
import com.glanci.mainModule
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

class AccountTest {

    @Test
    fun `getAccountsAfterTimestamp returns accounts`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "account").withService<AccountService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getAccountsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
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
    fun `saveAccounts returns success`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "account").withService<AccountService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getAccountsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(2, this.size)
        }

        val timestamp = getCurrentTimestamp()
        val accountsToSave = listOf(
            AccountCommandDto(
                id = 7617,
                orderNum = 6148,
                name = "Marshall Hines",
                currency = "USD",
                balance = 2.3,
                color = "ante",
                hide = false,
                hideBalance = false,
                withoutBalance = false,
                timestamp = timestamp,
                deleted = false
            ),
            AccountCommandDto(
                id = 7618,
                orderNum = 6149,
                name = "Marshall Hines",
                currency = "USD",
                balance = 2.3,
                color = "ante",
                hide = false,
                hideBalance = false,
                withoutBalance = false,
                timestamp = timestamp,
                deleted = false
            )
        )

        service.saveAccounts(accounts = accountsToSave, timestamp = timestamp, token = token).getErrorOrNull().run {
            assertNull(this)
        }

        service.getAccountsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
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
    fun `getAccountsAfterTimestamp returns AuthError InvalidToken`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "account").withService<AccountService>()

        service.getAccountsAfterTimestamp(timestamp = 0, token = "token").getErrorOrNull().run {
            assertNotNull(this)
            assertEquals(AuthError.InvalidToken, this)
        }

        client.close()
    }

}