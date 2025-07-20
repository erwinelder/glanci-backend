package com.glanci.budget.data.model

data class BudgetDataModel(
    val userId: Int,
    val id: Int,
    val amountLimit: Double,
    val categoryId: Int,
    val name: String,
    val repeatingPeriod: String,
    val timestamp: Long,
    val deleted: Boolean
)