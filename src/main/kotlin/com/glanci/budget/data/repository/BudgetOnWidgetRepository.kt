package com.glanci.budget.data.repository

import com.glanci.budget.data.model.BudgetOnWidgetDataModel

interface BudgetOnWidgetRepository {

    fun upsertBudgetsOnWidget(budgets: List<BudgetOnWidgetDataModel>)

    fun getBudgetsOnWidgetAfterTimestamp(userId: Int, timestamp: Long): List<BudgetOnWidgetDataModel>

}