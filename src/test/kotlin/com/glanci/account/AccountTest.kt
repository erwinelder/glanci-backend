package com.glanci.account

import com.glanci.account.shared.service.AccountService
import com.glanci.auth.domain.model.UserRole
import com.glanci.core.config.configureKrpc
import com.glanci.mainModule
import com.glanci.utils.getJwt
import io.ktor.http.*
import io.ktor.server.application.port
import io.ktor.server.testing.*
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AccountTest {

    @Test
    fun `test getAccountsAfterTimestamp`() = testApplication {
        application { mainModule() }
        val ktorClient = createClient {
            installKrpc {
                waitForServices = true
            }
        }
        val client: KtorRpcClient = ktorClient.rpc {
            url {
                host = "localhost"
                port = 80
                encodedPath = "account"
            }
            configureKrpc()
        }
        val token = getJwt(userId = 1, role = UserRole.User)

        val accountService = client.withService<AccountService>()

        val accounts = accountService.getAccountsAfterTimestamp(timestamp = 0, token = token)

        assertNotNull(accounts)
        assertEquals(accounts.size, 2)

        ktorClient.close()
    }

}