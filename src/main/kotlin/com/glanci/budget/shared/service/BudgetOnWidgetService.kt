package com.glanci.budget.shared.service

import com.glanci.budget.shared.dto.BudgetOnWidgetDto
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.RootError
import kotlinx.rpc.annotations.Rpc

@Rpc
interface BudgetOnWidgetService {

    suspend fun getUpdateTime(token: String): ResultData<Long, RootError>

    suspend fun saveBudgetsOnWidget(
        budgets: List<BudgetOnWidgetDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<RootError>

    suspend fun getBudgetsOnWidgetAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<BudgetOnWidgetDto>, RootError>

    suspend fun saveBudgetsOnWidgetAndGetAfterTimestamp(
        budgets: List<BudgetOnWidgetDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<BudgetOnWidgetDto>, RootError>

}