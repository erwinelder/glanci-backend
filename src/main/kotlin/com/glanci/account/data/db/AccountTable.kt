package com.glanci.account.data.db

import com.glanci.auth.data.db.GlanciUserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object AccountTable : Table("account") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val id = integer("id")
    val orderNum = integer("order_num")
    val name = varchar("name", 50)
    val currency = varchar("currency", 3)
    val balance = double("balance")
    val color = varchar("color", 20)
    val hide = bool("hide")
    val hideBalance = bool("hide_balance")
    val withoutBalance = bool("without_balance")
    val timestamp = long("timestamp")
    val deleted = bool("deleted")

    override val primaryKey = PrimaryKey(userId, id)
}