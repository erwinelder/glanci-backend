package com.glanci.budget.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUserResult
import com.glanci.budget.data.repository.BudgetRepository
import com.glanci.budget.mapper.toDataModel
import com.glanci.budget.mapper.toDto
import com.glanci.budget.shared.dto.BudgetWithAssociationsDto
import com.glanci.budget.shared.service.BudgetService
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.request.shared.ResultData
import com.glanci.request.shared.SimpleResult
import com.glanci.request.shared.getDataOrReturn
import com.glanci.request.shared.returnIfError
import com.glanci.request.shared.error.BudgetDataError
import com.glanci.request.shared.error.DataError

class BudgetServiceImpl(
    private val budgetRepository: BudgetRepository,
    private val updateTimeService: UpdateTimeService
) : BudgetService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveBudgetsWithAssociations(
        budgets: List<BudgetWithAssociationsDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        runCatching {
            budgetRepository.upsertBudgetsWithAssociations(
                userId = user.id,
                budgetsWithAssociations = budgets.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(BudgetDataError.BudgetsNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getBudgetsWithAssociationsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<BudgetWithAssociationsDto>, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }

        val budgetsWithAssociations = runCatching {
            budgetRepository.getBudgetsWithAssociationsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }.getOrElse {
            return ResultData.Error(BudgetDataError.BudgetsNotFetched)
        }

        return ResultData.Success(data = budgetsWithAssociations)
    }

    override suspend fun saveBudgetsWithAssociationsAndGetAfterTimestamp(
        budgets: List<BudgetWithAssociationsDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<BudgetWithAssociationsDto>, DataError> {
        saveBudgetsWithAssociations(budgets = budgets, timestamp = timestamp, token = token)
            .returnIfError { return ResultData.Error(it) }
        return getBudgetsWithAssociationsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}