package com.glanci.categoryCollection.data.db

import com.glanci.auth.data.db.GlanciUserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object CategoryCollectionTable : Table("category_collection") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val id = integer("id")
    val orderNum = integer("order_num")
    val type = varchar("type", 1)
    val name = varchar("name", 255)
    val timestamp = long("timestamp")
    val deleted = bool("deleted")

    override val primaryKey = PrimaryKey(userId, id)
}