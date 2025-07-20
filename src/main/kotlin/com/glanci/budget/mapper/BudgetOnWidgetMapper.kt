package com.glanci.budget.mapper

import com.glanci.budget.data.model.BudgetOnWidgetDataModel
import com.glanci.budget.shared.dto.BudgetOnWidgetDto


fun BudgetOnWidgetDto.toDataModel(userId: Int): BudgetOnWidgetDataModel {
    return BudgetOnWidgetDataModel(
        userId = userId,
        budgetId = budgetId,
        timestamp = timestamp,
        deleted = deleted
    )
}


fun BudgetOnWidgetDataModel.toDto(): BudgetOnWidgetDto {
    return BudgetOnWidgetDto(
        budgetId = budgetId,
        timestamp = timestamp,
        deleted = deleted
    )
}
