package com.glanci.core.domain.model.app

import kotlinx.serialization.Serializable

@Serializable
enum class TableName {
    Account, Category, Record, Transfer,
    CategoryCollection, Budget,
    BudgetOnWidget, Widget, NavigationButton
}