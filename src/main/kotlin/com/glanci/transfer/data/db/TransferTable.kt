package com.glanci.transfer.data.db

import com.glanci.account.data.db.AccountTable
import com.glanci.auth.data.db.GlanciUserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object TransferTable : Table("transfer") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val id = long("id")
    val date = long("date")
    val senderAccountId = integer("sender_account_id")
    val receiverAccountId = integer("receiver_account_id")
    val senderAmount = double("sender_amount")
    val receiverAmount = double("receiver_amount")
    val senderRate = double("sender_rate")
    val receiverRate = double("receiver_rate")
    val includeInBudgets = bool("include_in_budgets")
    val timestamp = long("timestamp")
    val deleted = bool("deleted")

    override val primaryKey = PrimaryKey(userId, id)

    init {
        foreignKey(userId, senderAccountId, target = AccountTable.primaryKey)
        foreignKey(userId, receiverAccountId, target = AccountTable.primaryKey)
    }
}