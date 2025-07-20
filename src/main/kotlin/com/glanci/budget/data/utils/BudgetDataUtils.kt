package com.glanci.budget.data.utils

import com.glanci.budget.data.model.BudgetAccountAssociationDataModel
import com.glanci.budget.data.model.BudgetDataModel
import com.glanci.budget.data.model.BudgetWithAssociationsDataModel


fun List<BudgetWithAssociationsDataModel>.divide(
): Pair<List<BudgetDataModel>, List<BudgetAccountAssociationDataModel>> {
    return map { it.asPair() }
        .unzip()
        .let { it.first to it.second.flatten() }
}

fun List<BudgetDataModel>.zipWithAssociations(
    associations: List<BudgetAccountAssociationDataModel>
): List<BudgetWithAssociationsDataModel> {
    return associateWith { budget ->
            if (!budget.deleted) associations.filter { it.budgetId == budget.id } else emptyList()
        }
        .map { (budget, associations) ->
            BudgetWithAssociationsDataModel(budget = budget, associations = associations)
        }
}
