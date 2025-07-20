package com.glanci.category

import com.glanci.auth.domain.model.UserRole
import com.glanci.category.shared.service.CategoryService
import com.glanci.mainModule
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CategoryTest {

    @Test
    fun `test getCategoriesAfterTimestamp`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "category")
        val categoryService = rcpClient.withService<CategoryService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        val categories = categoryService.getCategoriesAfterTimestamp(timestamp = 0, token = token)

        assertNotNull(categories)
        assertEquals(categories.size, 3)

        client.close()
    }

}