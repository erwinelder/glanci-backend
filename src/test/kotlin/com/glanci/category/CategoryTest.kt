package com.glanci.category

import com.glanci.auth.domain.model.UserRole
import com.glanci.category.shared.dto.CategoryCommandDto
import com.glanci.category.shared.service.CategoryService
import com.glanci.core.utils.getCurrentTimestamp
import com.glanci.mainModule
import com.glanci.request.shared.error.AuthDataError
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CategoryTest {

    @Test
    fun `getCategoriesAfterTimestamp returns categories`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "category").withService<CategoryService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getCategoriesAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(4, this.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `saveCategories returns success`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "category").withService<CategoryService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getCategoriesAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(4, this.size)
        }

        val timestamp = getCurrentTimestamp()
        val categoriesToSave = listOf(
            CategoryCommandDto(
                id = 9464,
                type = '-',
                orderNum = 9810,
                parentCategoryId = 5675,
                name = "Jaime Myers",
                iconName = "Susan Greer",
                colorName = "Quentin Clayton",
                timestamp = timestamp,
                deleted = false
            ),
            CategoryCommandDto(
                id = 9465,
                type = '+',
                orderNum = 9811,
                parentCategoryId = 5676,
                name = "John Doe",
                iconName = "Jane Smith",
                colorName = "Blue",
                timestamp = timestamp,
                deleted = false
            )
        )

        service.saveCategories(categories = categoriesToSave, timestamp = timestamp, token = token).getErrorOrNull().run {
            assertNull(this)
        }

        service.getCategoriesAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(6, this.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(timestamp, this)
        }

        client.close()
    }

    @Test
    fun `getCategoriesAfterTimestamp returns AuthError InvalidToken`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "category").withService<CategoryService>()

        service.getCategoriesAfterTimestamp(timestamp = 0, token = "token").getErrorOrNull().run {
            assertNotNull(this)
            assertEquals(AuthDataError.InvalidToken, this)
        }

        client.close()
    }

}