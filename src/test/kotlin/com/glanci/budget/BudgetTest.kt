package com.glanci.budget

import com.glanci.auth.shared.dto.UserRole
import com.glanci.budget.shared.dto.BudgetAccountAssociationDto
import com.glanci.budget.shared.dto.BudgetDto
import com.glanci.budget.shared.dto.BudgetWithAssociationsDto
import com.glanci.budget.shared.service.BudgetService
import com.glanci.core.utils.getCurrentTimestamp
import com.glanci.mainModule
import com.glanci.request.shared.error.AuthDataError
import com.glanci.request.shared.error.BudgetDataError
import com.glanci.utils.configureRcp
import com.glanci.utils.getJwt
import com.glanci.utils.getKrpcClient
import io.ktor.server.testing.*
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BudgetTest {

    @Test
    fun `getBudgetsWithAssociationsAfterTimestamp returns budgets`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "budget").withService<BudgetService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getBudgetsWithAssociationsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val associations = this?.flatMap { it.associations }

            assertNotNull(this)
            assertEquals(2, this.size)
            assertNotNull(associations)
            assertEquals(2, associations.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `saveBudgetsWithAssociations returns success`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "budget").withService<BudgetService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getBudgetsWithAssociationsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val associations = this?.flatMap { it.associations }

            assertNotNull(this)
            assertEquals(2, this.size)
            assertNotNull(associations)
            assertEquals(2, associations.size)
        }

        val timestamp = getCurrentTimestamp()
        val budgetsWithAssociationsToSave = listOf(
            BudgetWithAssociationsDto(
                budget = BudgetDto(
                    id = 8356,
                    amountLimit = 26.27,
                    categoryId = 1,
                    name = "Blanche Weaver",
                    repeatingPeriod = "sed",
                    timestamp = timestamp,
                    deleted = false
                ),
                associations = listOf(
                    BudgetAccountAssociationDto(budgetId = 8356, accountId = 1)
                )
            )
        )

        service.saveBudgetsWithAssociations(
            budgets = budgetsWithAssociationsToSave, timestamp = timestamp, token = token
        ).getErrorOrNull().run {
            assertNull(this)
        }

        service.getBudgetsWithAssociationsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val associations = this?.flatMap { it.associations }

            assertNotNull(this)
            assertEquals(3, this.size)
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
    fun `saveBudgetsWithAssociations returns BudgetError BudgetsNotSaved`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "budget").withService<BudgetService>()

        val token = getJwt(userId = 1, role = UserRole.User)

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        service.getBudgetsWithAssociationsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val associations = this?.flatMap { it.associations }

            assertNotNull(this)
            assertEquals(2, this.size)
            assertNotNull(associations)
            assertEquals(2, associations.size)
        }

        val timestamp = getCurrentTimestamp()
        val budgetsWithAssociationsToSave = listOf(
            BudgetWithAssociationsDto(
                budget = BudgetDto(
                    id = 8356,
                    amountLimit = 26.27,
                    categoryId = 100,
                    name = "Blanche Weaver",
                    repeatingPeriod = "sed",
                    timestamp = timestamp,
                    deleted = false
                ),
                associations = listOf(
                    BudgetAccountAssociationDto(budgetId = 8356, accountId = 1)
                )
            )
        )

        service.saveBudgetsWithAssociations(
            budgets = budgetsWithAssociationsToSave, timestamp = timestamp, token = token
        ).getErrorOrNull().run {
            assertNotNull(this)
            assertEquals(BudgetDataError.BudgetsNotSaved, this)
        }

        service.getBudgetsWithAssociationsAfterTimestamp(timestamp = 0, token = token).getDataOrNull().run {
            val associations = this?.flatMap { it.associations }

            assertNotNull(this)
            assertEquals(2, this.size)
            assertNotNull(associations)
            assertEquals(2, associations.size)
        }

        service.getUpdateTime(token = token).getDataOrNull().run {
            assertNotNull(this)
            assertEquals(0, this)
        }

        client.close()
    }

    @Test
    fun `getBudgetsWithAssociationsAfterTimestamp returns AuthError InvalidToken`() = testApplication {
        application { mainModule() }
        val client = getKrpcClient()
        val service = client.configureRcp(path = "budget").withService<BudgetService>()

        service.getBudgetsWithAssociationsAfterTimestamp(timestamp = 0, token = "token").getErrorOrNull().run {
            assertNotNull(this)
            assertEquals(AuthDataError.InvalidToken, this)
        }

        client.close()
    }

}