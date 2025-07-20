package com.glanci.categoryCollection.data.repository

import com.glanci.categoryCollection.data.db.CategoryCollectionCategoryAssociationTable
import com.glanci.categoryCollection.data.db.CategoryCollectionTable
import com.glanci.categoryCollection.data.model.CategoryCollectionCategoryAssociationDataModel
import com.glanci.categoryCollection.data.model.CategoryCollectionDataModel
import com.glanci.categoryCollection.data.model.CategoryCollectionWithAssociationsDataModel
import com.glanci.categoryCollection.data.utils.divide
import com.glanci.categoryCollection.data.utils.zipWithAssociations
import com.glanci.core.data.db.GlanciDatabaseProvider
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryCollectionRepositoryImpl(
    private val database: Database
) : CategoryCollectionRepository {

    constructor(databaseProvider: GlanciDatabaseProvider) : this(database = databaseProvider.database)


    override fun upsertCategoryCollectionsWithAssociations(
        collections: List<CategoryCollectionWithAssociationsDataModel>
    ) {
        val (collections, associations) = collections.divide()

        transaction(database) {
            CategoryCollectionTable.batchUpsert(collections) { collection ->
                this[CategoryCollectionTable.userId] = collection.userId
                this[CategoryCollectionTable.id] = collection.id
                this[CategoryCollectionTable.orderNum] = collection.orderNum
                this[CategoryCollectionTable.type] = collection.type.toString()
                this[CategoryCollectionTable.name] = collection.name
                this[CategoryCollectionTable.timestamp] = collection.timestamp
                this[CategoryCollectionTable.deleted] = collection.deleted
            }
            CategoryCollectionCategoryAssociationTable.batchUpsert(associations) { association ->
                this[CategoryCollectionCategoryAssociationTable.userId] = association.userId
                this[CategoryCollectionCategoryAssociationTable.collectionId] = association.collectionId
                this[CategoryCollectionCategoryAssociationTable.categoryId] = association.categoryId
            }
        }
    }

    private fun getCategoryCollectionsAfterTimestamp(
        userId: Int,
        timestamp: Long
    ): List<CategoryCollectionDataModel> {
        return transaction(database) {
            CategoryCollectionTable
                .selectAll()
                .where {
                    (CategoryCollectionTable.userId eq userId) and (CategoryCollectionTable.timestamp greater timestamp)
                }
                .map {
                    CategoryCollectionDataModel(
                        userId = it[CategoryCollectionTable.userId],
                        id = it[CategoryCollectionTable.id],
                        orderNum = it[CategoryCollectionTable.orderNum],
                        type = it[CategoryCollectionTable.type][0],
                        name = it[CategoryCollectionTable.name],
                        timestamp = it[CategoryCollectionTable.timestamp],
                        deleted = it[CategoryCollectionTable.deleted]
                    )
                }
        }
    }

    private fun getAssociationsByCollectionIds(
        userId: Int,
        collectionIds: List<Int>
    ): List<CategoryCollectionCategoryAssociationDataModel> {
        return transaction(database) {
            CategoryCollectionCategoryAssociationTable
                .selectAll()
                .where {
                    (CategoryCollectionCategoryAssociationTable.userId eq userId) and
                            (CategoryCollectionCategoryAssociationTable.collectionId inList collectionIds)
                }
                .map {
                    CategoryCollectionCategoryAssociationDataModel(
                        userId = it[CategoryCollectionCategoryAssociationTable.userId],
                        collectionId = it[CategoryCollectionCategoryAssociationTable.collectionId],
                        categoryId = it[CategoryCollectionCategoryAssociationTable.categoryId]
                    )
                }
        }
    }

    override fun getCategoryCollectionsWithAssociationsAfterTimestamp(
        userId: Int,
        timestamp: Long
    ): List<CategoryCollectionWithAssociationsDataModel> {
        val collections = getCategoryCollectionsAfterTimestamp(userId = userId, timestamp = timestamp)
        val ids = collections.map { it.id }
        val associations = getAssociationsByCollectionIds(userId = userId, collectionIds = ids)

        return collections.zipWithAssociations(associations = associations)
    }

}