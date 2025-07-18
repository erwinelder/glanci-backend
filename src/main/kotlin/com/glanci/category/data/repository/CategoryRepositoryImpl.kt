package com.glanci.category.data.repository

import com.glanci.category.data.db.CategoryTable
import com.glanci.category.data.model.CategoryDataModel
import com.glanci.core.data.db.GlanciDatabaseProvider
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryRepositoryImpl(
    private val database: Database
) : CategoryRepository {

    constructor(databaseProvider: GlanciDatabaseProvider) : this(database = databaseProvider.database)


    override fun upsertCategories(categories: List<CategoryDataModel>) {
        transaction(database) {
            CategoryTable.batchUpsert(categories) { category ->
                this[CategoryTable.userId] = category.userId
                this[CategoryTable.id] = category.id
                this[CategoryTable.type] = category.type.toString()
                this[CategoryTable.orderNum] = category.orderNum
                this[CategoryTable.parentCategoryId] = category.parentCategoryId
                this[CategoryTable.name] = category.name
                this[CategoryTable.iconName] = category.iconName
                this[CategoryTable.colorName] = category.colorName
                this[CategoryTable.timestamp] = category.timestamp
                this[CategoryTable.deleted] = category.deleted
            }
        }
    }

    override fun getCategoriesAfterTimestamp(userId: Int, timestamp: Long): List<CategoryDataModel> {
        return transaction(database) {
            CategoryTable
                .selectAll()
                .where {
                    (CategoryTable.userId eq userId) and (CategoryTable.timestamp greater timestamp)
                }
                .map {
                    CategoryDataModel(
                        userId = it[CategoryTable.userId],
                        id = it[CategoryTable.id],
                        type = it[CategoryTable.type][0],
                        orderNum = it[CategoryTable.orderNum],
                        parentCategoryId = it[CategoryTable.parentCategoryId],
                        name = it[CategoryTable.name],
                        iconName = it[CategoryTable.iconName],
                        colorName = it[CategoryTable.colorName],
                        timestamp = it[CategoryTable.timestamp],
                        deleted = it[CategoryTable.deleted]
                    )
                }
        }
    }

}