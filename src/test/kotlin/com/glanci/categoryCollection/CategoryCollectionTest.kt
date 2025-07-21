package com.glanci.categoryCollection

import com.glanci.auth.domain.model.UserRole
import com.glanci.categoryCollection.shared.service.CategoryCollectionService
import com.glanci.mainModule
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CategoryCollectionTest {

    @Test
    fun `test getCategoryCollectionsWithAssociationsAfterTimestamp`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "categoryCollection")
        val service = rcpClient.withService<CategoryCollectionService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        val collectionsWithAssociations = service.getCategoryCollectionsWithAssociationsAfterTimestamp(
            timestamp = 0, token = token
        )
        val associations = collectionsWithAssociations?.flatMap { it.associations }

        assertNotNull(collectionsWithAssociations)
        assertEquals(collectionsWithAssociations.size, 1)
        assertNotNull(associations)
        assertEquals(associations.size, 1)

        client.close()
    }

}