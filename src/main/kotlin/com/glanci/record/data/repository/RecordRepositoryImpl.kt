package com.glanci.record.data.repository

import com.glanci.core.data.db.GlanciDatabaseProvider
import com.glanci.record.data.db.RecordItemTable
import com.glanci.record.data.db.RecordTable
import com.glanci.record.data.model.RecordDataModel
import com.glanci.record.data.model.RecordItemDataModel
import com.glanci.record.data.model.RecordWithItemsDataModel
import com.glanci.record.data.utils.divide
import com.glanci.record.data.utils.zipWithItems
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class RecordRepositoryImpl(
    private val database: Database
) : RecordRepository {

    constructor(databaseProvider: GlanciDatabaseProvider) : this(database = databaseProvider.database)


    override fun upsertRecordsWithItems(recordsWithItems: List<RecordWithItemsDataModel>) {
        val (records, items) = recordsWithItems.divide()

        transaction(database) {
            RecordTable.batchUpsert(records) { record ->
                this[RecordTable.userId] = record.userId
                this[RecordTable.id] = record.id
                this[RecordTable.date] = record.date
                this[RecordTable.type] = record.type.toString()
                this[RecordTable.accountId] = record.accountId
                this[RecordTable.includeInBudgets] = record.includeInBudgets
                this[RecordTable.timestamp] = record.timestamp
                this[RecordTable.deleted] = record.deleted
            }
            RecordItemTable.batchUpsert(items) { item ->
                this[RecordItemTable.userId] = item.userId
                this[RecordItemTable.id] = item.id
                this[RecordItemTable.recordId] = item.recordId
                this[RecordItemTable.totalAmount] = item.totalAmount
                this[RecordItemTable.quantity] = item.quantity
                this[RecordItemTable.categoryId] = item.categoryId
                this[RecordItemTable.subcategoryId] = item.subcategoryId
                this[RecordItemTable.note] = item.note
            }
        }
    }

    private fun getRecordsAfterTimestamp(userId: Int, timestamp: Long): List<RecordDataModel> {
        return transaction(database) {
            RecordTable
                .selectAll()
                .where {
                    (RecordTable.userId eq userId) and (RecordTable.timestamp greater timestamp)
                }
                .map {
                    RecordDataModel(
                        userId = it[RecordTable.userId],
                        id = it[RecordTable.id],
                        date = it[RecordTable.date],
                        type = it[RecordTable.type][0],
                        accountId = it[RecordTable.accountId],
                        includeInBudgets = it[RecordTable.includeInBudgets],
                        timestamp = it[RecordTable.timestamp],
                        deleted = it[RecordTable.deleted]
                    )
                }
        }
    }

    private fun getRecordItemsByRecordIds(userId: Int, recordIds: List<Long>): List<RecordItemDataModel> {
        return transaction(database) {
            RecordItemTable
                .selectAll()
                .where {
                    (RecordItemTable.userId eq userId) and (RecordItemTable.recordId inList recordIds)
                }
                .map {
                    RecordItemDataModel(
                        userId = it[RecordItemTable.userId],
                        id = it[RecordItemTable.id],
                        recordId = it[RecordItemTable.recordId],
                        totalAmount = it[RecordItemTable.totalAmount],
                        quantity = it[RecordItemTable.quantity],
                        categoryId = it[RecordItemTable.categoryId],
                        subcategoryId = it[RecordItemTable.subcategoryId],
                        note = it[RecordItemTable.note]
                    )
                }
        }
    }

    override fun getRecordsWithItemsAfterTimestamp(
        userId: Int,
        timestamp: Long
    ): List<RecordWithItemsDataModel> {
        val records = getRecordsAfterTimestamp(userId = userId, timestamp = timestamp)
        val ids = records.map { it.id }
        val items = getRecordItemsByRecordIds(userId = userId, recordIds = ids)

        return records.zipWithItems(items = items)
    }

}