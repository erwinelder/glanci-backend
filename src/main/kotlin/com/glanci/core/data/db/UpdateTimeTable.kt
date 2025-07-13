package com.glanci.core.data.db

import com.glanci.auth.data.db.GlanciUserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UpdateTimeTable : Table("update_time") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("table_name", 30)
    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(userId, name)
}