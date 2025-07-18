package com.glanci.category.data.db

import com.glanci.auth.data.db.GlanciUserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object CategoryTable : Table("category") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val id = integer("id")
    val type = varchar("type", 1)
    val orderNum = integer("order_num")
    val parentCategoryId = integer("parent_category_id").nullable()
    val name = varchar("name", 255)
    val iconName = varchar("icon_name", 50)
    val colorName = varchar("color_name", 50)
    val timestamp = long("timestamp")
    val deleted = bool("deleted")

    override val primaryKey = PrimaryKey(userId, id)
}