package com.glanci.category

import com.glanci.auth.domain.model.UserRole
import com.glanci.category.shared.service.CategoryService
import com.glanci.core.config.configureKrpc
import com.glanci.mainModule
import com.glanci.utils.getJwt
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CategoryTest {

    @Test
    fun `test getCategoriesAfterTimestamp`() = testApplication {
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
                encodedPath = "category"
            }
            configureKrpc()
        }
        val token = getJwt(userId = 1, role = UserRole.User)

        val categoryService = client.withService<CategoryService>()

        val categories = categoryService.getCategoriesAfterTimestamp(timestamp = 0, token = token)

        assertNotNull(categories)
        assertEquals(categories.size, 3)

        ktorClient.close()
    }

}