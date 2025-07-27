package com.glanci.categoryCollection

import com.glanci.auth.domain.model.UserRole
import com.glanci.categoryCollection.shared.dto.CategoryCollectionCategoryAssociationDto
import com.glanci.categoryCollection.shared.dto.CategoryCollectionDto
import com.glanci.categoryCollection.shared.dto.CategoryCollectionWithAssociationsDto
import com.glanci.categoryCollection.shared.service.CategoryCollectionService
import com.glanci.core.utils.getCurrentTimestamp
import com.glanci.mainModule
import com.glanci.request.domain.error.AuthDataError
import com.glanci.request.domain.error.CategoryCollectionError
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CategoryCollectionTest {

    @Test
    fun `getCategoryCollectionsWithAssociationsAfterTimestamp returns category collections`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "categoryCollection").withService<CategoryCollectionService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getCategoryCollectionsWithAssociationsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val associations = this?.flatMap { it.associations }

            assertNotNull(this)
            assertEquals(1, this.size)
            assertNotNull(associations)
            assertEquals(1, associations.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `saveCategoryCollectionsWithAssociations returns success`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "categoryCollection").withService<CategoryCollectionService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getCategoryCollectionsWithAssociationsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val associations = this?.flatMap { it.associations }

            assertNotNull(this)
            assertEquals(1, this.size)
            assertNotNull(associations)
            assertEquals(1, associations.size)
        }

        val timestamp = getCurrentTimestamp()
        val collectionsWithAssociationsToSave = listOf(
            CategoryCollectionWithAssociationsDto(
                collection = CategoryCollectionDto(
                    id = 2868,
                    orderNum = 1726,
                    type = '-',
                    name = "Ahmad Phelps",
                    timestamp = timestamp,
                    deleted = false
                ),
                associations = listOf(
                    CategoryCollectionCategoryAssociationDto(collectionId = 2868, categoryId = 1),
                    CategoryCollectionCategoryAssociationDto(collectionId = 2868, categoryId = 2),
                )
            )
        )

        service.saveCategoryCollectionsWithAssociations(
            collections = collectionsWithAssociationsToSave, timestamp = timestamp, token = token
        ).getErrorOrNull().run {
            assertNull(this)
        }

        service.getCategoryCollectionsWithAssociationsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val associations = this?.flatMap { it.associations }

            assertNotNull(this)
            assertEquals(2, this.size)
            assertNotNull(associations)
            assertEquals(3, associations.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(timestamp, this)
        }

        client.close()
    }

    @Test
    fun `saveCategoryCollectionsWithAssociations returns CategoryCollectionError CategoryCollectionsNotSaved`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "categoryCollection").withService<CategoryCollectionService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getCategoryCollectionsWithAssociationsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val associations = this?.flatMap { it.associations }

            assertNotNull(this)
            assertEquals(1, this.size)
            assertNotNull(associations)
            assertEquals(1, associations.size)
        }

        val timestamp = getCurrentTimestamp()
        val collectionsWithAssociationsToSave = listOf(
            CategoryCollectionWithAssociationsDto(
                collection = CategoryCollectionDto(
                    id = 2868,
                    orderNum = 1726,
                    type = '-',
                    name = "Ahmad Phelps",
                    timestamp = timestamp,
                    deleted = false
                ),
                associations = listOf(
                    CategoryCollectionCategoryAssociationDto(collectionId = 2868, categoryId = 100),
                    CategoryCollectionCategoryAssociationDto(collectionId = 2868, categoryId = 2),
                )
            )
        )

        service.saveCategoryCollectionsWithAssociations(
            collections = collectionsWithAssociationsToSave, timestamp = timestamp, token = token
        ).getErrorOrNull().run {
            assertEquals(CategoryCollectionError.CategoryCollectionsNotSaved, this)
        }

        service.getCategoryCollectionsWithAssociationsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val associations = this?.flatMap { it.associations }

            assertNotNull(this)
            assertEquals(1, this.size)
            assertNotNull(associations)
            assertEquals(1, associations.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `getCategoryCollectionsWithAssociationsAfterTimestamp returns AuthError InvalidToken`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "categoryCollection").withService<CategoryCollectionService>()

        service.getCategoryCollectionsWithAssociationsAfterTimestamp(timestamp = 0, token = "token").getErrorOrNull().run {
            assertNotNull(this)
            assertEquals(AuthDataError.InvalidToken, this)
        }

        client.close()
    }

}