package com.glanci.budget.data.model

data class BudgetOnWidgetDataModel(
    val userId: Int,
    val budgetId: Int,
    val timestamp: Long,
    val deleted: Boolean
)
