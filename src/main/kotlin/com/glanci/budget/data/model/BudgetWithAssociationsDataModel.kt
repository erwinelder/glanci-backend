package com.glanci.budget.data.model

data class BudgetWithAssociationsDataModel(
    val budget: BudgetDataModel,
    val associations: List<BudgetAccountAssociationDataModel>
) {

    fun asPair(): Pair<BudgetDataModel, List<BudgetAccountAssociationDataModel>> {
        return budget to associations.takeUnless { budget.deleted }.orEmpty()
    }

}