package com.glanci.core.domain.dto

import kotlinx.serialization.Serializable

@Serializable
enum class TableName {
    Account, Category, Record, Transfer,
    CategoryCollection, Budget,
    BudgetOnWidget, Widget, NavigationButton
}