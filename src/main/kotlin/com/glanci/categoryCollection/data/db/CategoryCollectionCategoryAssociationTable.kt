package com.glanci.categoryCollection.data.db

import com.glanci.auth.data.db.GlanciUserTable
import com.glanci.category.data.db.CategoryTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object CategoryCollectionCategoryAssociationTable : Table("category_collection_category_association") {
    val userId = integer("user_id").references(GlanciUserTable.id, onDelete = ReferenceOption.CASCADE)
    val collectionId = integer("collection_id")
    val categoryId = integer("category_id")

    override val primaryKey = PrimaryKey(userId, collectionId, categoryId)

    init {
        foreignKey(userId, collectionId, target = CategoryCollectionTable.primaryKey, onDelete = ReferenceOption.CASCADE)
        foreignKey(userId, categoryId, target = CategoryTable.primaryKey, onDelete = ReferenceOption.CASCADE)
    }
}