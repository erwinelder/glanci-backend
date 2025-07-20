package com.glanci.budget.data.repository

import com.glanci.budget.data.model.BudgetWithAssociationsDataModel

interface BudgetRepository {

    fun upsertBudgetsWithAssociations(budgetsWithAssociations: List<BudgetWithAssociationsDataModel>)

    fun getBudgetsWithAssociationsAfterTimestamp(userId: Int, timestamp: Long): List<BudgetWithAssociationsDataModel>

}