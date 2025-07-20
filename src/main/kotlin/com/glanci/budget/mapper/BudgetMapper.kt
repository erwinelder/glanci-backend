package com.glanci.budget.mapper

import com.glanci.budget.data.model.BudgetAccountAssociationDataModel
import com.glanci.budget.data.model.BudgetDataModel
import com.glanci.budget.data.model.BudgetWithAssociationsDataModel
import com.glanci.budget.shared.dto.BudgetAccountAssociationDto
import com.glanci.budget.shared.dto.BudgetDto
import com.glanci.budget.shared.dto.BudgetWithAssociationsDto


fun BudgetDto.toDataModel(userId: Int): BudgetDataModel {
    return BudgetDataModel(
        userId = userId,
        id = id,
        amountLimit = amountLimit,
        categoryId = categoryId,
        name = name,
        repeatingPeriod = repeatingPeriod,
        timestamp = timestamp,
        deleted = deleted
    )
}

fun BudgetAccountAssociationDto.toDataModel(userId: Int): BudgetAccountAssociationDataModel {
    return BudgetAccountAssociationDataModel(
        userId = userId,
        budgetId = budgetId,
        accountId = accountId
    )
}

fun BudgetWithAssociationsDto.toDataModel(userId: Int): BudgetWithAssociationsDataModel {
    return BudgetWithAssociationsDataModel(
        budget = budget.toDataModel(userId = userId),
        associations = associations.map { it.toDataModel(userId = userId) }
    )
}


fun BudgetDataModel.toDto(): BudgetDto {
    return BudgetDto(
        id = id,
        amountLimit = amountLimit,
        categoryId = categoryId,
        name = name,
        repeatingPeriod = repeatingPeriod,
        timestamp = timestamp,
        deleted = deleted
    )
}

fun BudgetAccountAssociationDataModel.toDto(): BudgetAccountAssociationDto {
    return BudgetAccountAssociationDto(
        budgetId = budgetId,
        accountId = accountId
    )
}

fun BudgetWithAssociationsDataModel.toDto(): BudgetWithAssociationsDto {
    return BudgetWithAssociationsDto(
        budget = budget.toDto(),
        associations = associations.map { it.toDto() }
    )
}