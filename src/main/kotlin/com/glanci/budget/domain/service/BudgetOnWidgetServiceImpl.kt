package com.glanci.budget.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.budget.data.repository.BudgetOnWidgetRepository
import com.glanci.budget.error.BudgetOnWidgetError
import com.glanci.budget.mapper.toDataModel
import com.glanci.budget.mapper.toDto
import com.glanci.budget.shared.dto.BudgetOnWidgetDto
import com.glanci.budget.shared.service.BudgetOnWidgetService
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.domain.dto.TableName
import com.glanci.core.error.UpdateTimeException

class BudgetOnWidgetServiceImpl(
    private val budgetOnWidgetRepository: BudgetOnWidgetRepository,
    private val updateTimeRepository: UpdateTimeRepository
) : BudgetOnWidgetService {

    private val tableName = TableName.BudgetOnWidget


    override suspend fun getUpdateTime(token: String): Long? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            updateTimeRepository.getUpdateTime(userId = user.id, tableName = tableName) ?: 0
        }
            .onFailure { throw UpdateTimeException.UpdateTimeNotFetched() }
            .getOrNull()
    }

    private fun saveUpdateTime(timestamp: Long, userId: Int) {
        runCatching {
            updateTimeRepository.saveUpdateTime(userId = userId, tableName = tableName, timestamp = timestamp)
        }
            .onFailure { throw UpdateTimeException.UpdateTimeNotSaved() }
    }

    override suspend fun saveBudgetsOnWidget(
        budgets: List<BudgetOnWidgetDto>,
        timestamp: Long,
        token: String
    ) {
        val user = authorizeAtLeastAsUser(token = token)

        runCatching {
            budgetOnWidgetRepository.upsertBudgetsOnWidget(
                budgets = budgets.map { it.toDataModel(userId = user.id) }
            )
        }
            .onFailure { throw BudgetOnWidgetError.BudgetsOnWidgetNotSaved() }

        saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getBudgetsOnWidgetAfterTimestamp(
        timestamp: Long,
        token: String
    ): List<BudgetOnWidgetDto>? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            budgetOnWidgetRepository.getBudgetsOnWidgetAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }
            .onFailure { throw BudgetOnWidgetError.BudgetsOnWidgetNotFetched() }
            .getOrNull()
    }

    override suspend fun saveBudgetsOnWidgetAndGetAfterTimestamp(
        budgets: List<BudgetOnWidgetDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): List<BudgetOnWidgetDto>? {
        saveBudgetsOnWidget(budgets = budgets, timestamp = timestamp, token = token)
        return getBudgetsOnWidgetAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}