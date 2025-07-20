package com.glanci.budget

import com.glanci.auth.domain.model.UserRole
import com.glanci.budget.shared.service.BudgetService
import com.glanci.mainModule
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BudgetTest {

    @Test
    fun `test getBudgetsWithAssociationsAfterTimestamp`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "budget")
        val budgetService = rcpClient.withService<BudgetService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        val budgetsWithAssociations = budgetService.getBudgetsWithAssociationsAfterTimestamp(
            timestamp = 0, token = token
        )
        val associations = budgetsWithAssociations?.flatMap { it.associations }

        assertNotNull(budgetsWithAssociations)
        assertEquals(budgetsWithAssociations.size, 2)
        assertNotNull(associations)
        assertEquals(associations.size, 2)

        client.close()
    }

}