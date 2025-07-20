package com.glanci.budget.data.db

import com.glanci.auth.data.db.GlanciUserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object BudgetOnWidgetTable : Table("budget_on_widget") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val budgetId = integer("budget_id")
    val timestamp = long("timestamp")
    val deleted = bool("deleted")

    override val primaryKey = PrimaryKey(userId, budgetId)

    init {
        foreignKey(userId, budgetId, target = BudgetTable.primaryKey, onDelete = ReferenceOption.CASCADE)
    }
}