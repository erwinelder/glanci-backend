package com.glanci.budget

import com.glanci.auth.domain.model.UserRole
import com.glanci.budget.shared.dto.BudgetOnWidgetDto
import com.glanci.budget.shared.service.BudgetOnWidgetService
import com.glanci.core.utils.getCurrentTimestamp
import com.glanci.mainModule
import com.glanci.request.domain.error.AuthError
import com.glanci.request.domain.error.BudgetOnWidgetError
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BudgetOnWidgetTest {

    @Test
    fun `getBudgetsOnWidgetAfterTimestamp returns budgets on widget`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "budget").withService<BudgetOnWidgetService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getBudgetsOnWidgetAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(1, this.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `saveBudgetsOnWidget returns success`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "budget").withService<BudgetOnWidgetService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getBudgetsOnWidgetAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(1, this.size)
        }

        val timestamp = getCurrentTimestamp()
        val budgetsToSave = listOf(
            BudgetOnWidgetDto(budgetId = 2, timestamp = timestamp, deleted = false)
        )

        service.saveBudgetsOnWidget(budgets = budgetsToSave, timestamp = timestamp, token = token).getErrorOrNull().run {
            assertNull(this)
        }

        service.getBudgetsOnWidgetAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(2, this.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(timestamp, this)
        }

        client.close()
    }

    @Test
    fun `saveBudgetsOnWidget returns BudgetOnWidgetError BudgetsOnWidgetNotSaved`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "budget").withService<BudgetOnWidgetService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getBudgetsOnWidgetAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(1, this.size)
        }

        val timestamp = getCurrentTimestamp()
        val budgetsToSave = listOf(
            BudgetOnWidgetDto(budgetId = 100, timestamp = timestamp, deleted = false)
        )

        service.saveBudgetsOnWidget(budgets = budgetsToSave, timestamp = timestamp, token = token).getErrorOrNull().run {
            assertNotNull(this)
            assertEquals(BudgetOnWidgetError.BudgetsOnWidgetNotSaved, this)
        }

        service.getBudgetsOnWidgetAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(1, this.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `getBudgetsOnWidgetAfterTimestamp returns AuthError InvalidToken`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "budget").withService<BudgetOnWidgetService>()

        service.getBudgetsOnWidgetAfterTimestamp(timestamp = 0, token = "token").getErrorOrNull().run {
            assertNotNull(this)
            assertEquals(AuthError.InvalidToken, this)
        }

        client.close()
    }

}