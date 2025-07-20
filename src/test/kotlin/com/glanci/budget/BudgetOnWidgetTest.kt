package com.glanci.budget

import com.glanci.auth.domain.model.UserRole
import com.glanci.budget.shared.service.BudgetOnWidgetService
import com.glanci.mainModule
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BudgetOnWidgetTest {

    @Test
    fun `test getBudgetsOnWidgetAfterTimestamp`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val rcpClient = client.configureRcp(path = "budget")
        val budgetOnWidgetService = rcpClient.withService<BudgetOnWidgetService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        val budgetsOnWidget = budgetOnWidgetService.getBudgetsOnWidgetAfterTimestamp(timestamp = 0, token = token)

        assertNotNull(budgetsOnWidget)
        assertEquals(budgetsOnWidget.size, 2)

        client.close()
    }

}