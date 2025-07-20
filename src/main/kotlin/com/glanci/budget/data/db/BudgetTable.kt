package com.glanci.budget.data.db

import com.glanci.auth.data.db.GlanciUserTable
import com.glanci.category.data.db.CategoryTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object BudgetTable : Table("budget") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val id = integer("id")
    val amountLimit = double("amount_limit")
    val categoryId = integer("category_id")
    val name = varchar("name", 255)
    val repeatingPeriod = varchar("repeating_period", 50)
    val timestamp = long("timestamp")
    val deleted = bool("deleted")

    override val primaryKey = PrimaryKey(userId, id)

    init {
        foreignKey(userId, categoryId, target = CategoryTable.primaryKey)
    }
}