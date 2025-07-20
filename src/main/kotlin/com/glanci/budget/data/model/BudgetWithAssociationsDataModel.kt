package com.glanci.budget.data.model

data class BudgetWithAssociationsDataModel(
    val budget: BudgetDataModel,
    val associations: List<BudgetAccountAssociationDataModel>
) {

    fun replaceUserId(userId: Int): BudgetWithAssociationsDataModel {
        return BudgetWithAssociationsDataModel(
            budget = budget.copy(userId = userId),
            associations = associations.map { it.copy(userId = userId) }
        )
    }

    fun asPair(): Pair<BudgetDataModel, List<BudgetAccountAssociationDataModel>> {
        return budget to associations.takeUnless { budget.deleted }.orEmpty()
    }

}