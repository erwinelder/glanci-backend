package com.glanci.record.data.db

import com.glanci.auth.data.db.GlanciUserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object RecordItemTable : Table("record_item") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val id = long("id")
    val recordId = long("record_id")
    val totalAmount = double("amount")
    val quantity = integer("quantity").nullable()
    val categoryId = integer("category_id")
    val subcategoryId = integer("subcategory_id").nullable()
    val note = varchar("note", 255).nullable()

    override val primaryKey = PrimaryKey(userId, id)

    init {
        foreignKey(userId, recordId, target = RecordTable.primaryKey, onDelete = ReferenceOption.CASCADE)
    }
}