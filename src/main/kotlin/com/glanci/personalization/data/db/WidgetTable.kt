package com.glanci.personalization.data.db

import com.glanci.auth.data.db.GlanciUserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object WidgetTable : Table("widget") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 50)
    val orderNum = integer("order_num")
    val timestamp = long("timestamp")
    val deleted = bool("deleted")

    override val primaryKey = PrimaryKey(userId, name)
}