package com.glanci.budget.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.budget.data.repository.BudgetRepository
import com.glanci.budget.shared.dto.BudgetWithAssociationsDto
import com.glanci.budget.error.BudgetError
import com.glanci.budget.mapper.toDataModel
import com.glanci.budget.mapper.toDto
import com.glanci.budget.shared.service.BudgetService
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.domain.dto.TableName
import com.glanci.core.error.UpdateTimeError

class BudgetServiceImpl(
    private val budgetRepository: BudgetRepository,
    private val updateTimeRepository: UpdateTimeRepository
) : BudgetService {

    private val tableName = TableName.Budget


    override suspend fun getUpdateTime(token: String): Long? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            updateTimeRepository.getUpdateTime(userId = user.id, tableName = tableName) ?: 0
        }
            .onFailure { throw UpdateTimeError.UpdateTimeNotFetched() }
            .getOrNull()
    }

    private fun saveUpdateTime(timestamp: Long, userId: Int) {
        runCatching {
            updateTimeRepository.saveUpdateTime(userId = userId, tableName = tableName, timestamp = timestamp)
        }
            .onFailure { throw UpdateTimeError.UpdateTimeNotSaved() }
    }

    override suspend fun saveBudgetsWithAssociations(
        budgets: List<BudgetWithAssociationsDto>,
        timestamp: Long,
        token: String
    ) {
        val user = authorizeAtLeastAsUser(token = token)

        runCatching {
            budgetRepository.upsertBudgetsWithAssociations(
                budgetsWithAssociations = budgets.map { it.toDataModel(userId = user.id) }
            )
        }
            .onFailure { throw BudgetError.BudgetsNotSaved() }

        saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getBudgetsWithAssociationsAfterTimestamp(
        timestamp: Long,
        token: String
    ): List<BudgetWithAssociationsDto>? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            budgetRepository.getBudgetsWithAssociationsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }
            .onFailure { throw BudgetError.BudgetsNotFetched() }
            .getOrNull()
    }

    override suspend fun saveBudgetsWithAssociationsAndGetAfterTimestamp(
        budgets: List<BudgetWithAssociationsDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): List<BudgetWithAssociationsDto>? {
        saveBudgetsWithAssociations(budgets = budgets, timestamp = timestamp, token = token)
        return getBudgetsWithAssociationsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}