package com.glanci.budget.data.db

import com.glanci.account.data.db.AccountTable
import com.glanci.auth.data.db.GlanciUserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object BudgetAccountAssociationTable : Table("budget_account_association") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val budgetId = integer("budget_id")
    val accountId = integer("account_id")

    override val primaryKey = PrimaryKey(userId, budgetId, accountId)

    init {
        foreignKey(userId, budgetId, target = BudgetTable.primaryKey)
        foreignKey(userId, accountId, target = AccountTable.primaryKey)
    }
}