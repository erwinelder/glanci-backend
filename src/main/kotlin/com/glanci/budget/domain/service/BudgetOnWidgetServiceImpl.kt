package com.glanci.budget.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUserResult
import com.glanci.budget.data.repository.BudgetOnWidgetRepository
import com.glanci.budget.mapper.toDataModel
import com.glanci.budget.mapper.toDto
import com.glanci.budget.shared.dto.BudgetOnWidgetDto
import com.glanci.budget.shared.service.BudgetOnWidgetService
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.BudgetOnWidgetError
import com.glanci.request.domain.error.DataError
import com.glanci.request.domain.getDataOrReturn
import com.glanci.request.domain.returnIfError

class BudgetOnWidgetServiceImpl(
    private val budgetOnWidgetRepository: BudgetOnWidgetRepository,
    private val updateTimeService: UpdateTimeService
) : BudgetOnWidgetService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveBudgetsOnWidget(
        budgets: List<BudgetOnWidgetDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        runCatching {
            budgetOnWidgetRepository.upsertBudgetsOnWidget(
                budgets = budgets.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(BudgetOnWidgetError.BudgetsOnWidgetNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getBudgetsOnWidgetAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<BudgetOnWidgetDto>, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }

        val budgets = runCatching {
            budgetOnWidgetRepository.getBudgetsOnWidgetAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }.getOrElse {
            return ResultData.Error(BudgetOnWidgetError.BudgetsOnWidgetNotFetched)
        }

        return ResultData.Success(data = budgets)
    }

    override suspend fun saveBudgetsOnWidgetAndGetAfterTimestamp(
        budgets: List<BudgetOnWidgetDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<BudgetOnWidgetDto>, DataError> {
        saveBudgetsOnWidget(budgets = budgets, timestamp = timestamp, token = token)
            .returnIfError { return ResultData.Error(it) }
        return getBudgetsOnWidgetAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}