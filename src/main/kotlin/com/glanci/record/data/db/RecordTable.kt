package com.glanci.record.data.db

import com.glanci.account.data.db.AccountTable
import com.glanci.auth.data.db.GlanciUserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object RecordTable : Table("record") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val id = long("id")
    val date = long("date")
    val type = varchar("type", 1)
    val accountId = integer("account_id")
    val includeInBudgets = bool("include_in_budgets")
    val timestamp = long("timestamp")
    val deleted = bool("deleted")

    override val primaryKey = PrimaryKey(userId, id)

    init {
        foreignKey(userId, accountId, target = AccountTable.primaryKey, onDelete = ReferenceOption.CASCADE)
    }
}