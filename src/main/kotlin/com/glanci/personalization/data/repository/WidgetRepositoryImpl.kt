package com.glanci.personalization.data.repository

import com.glanci.core.data.db.GlanciDatabaseProvider
import com.glanci.personalization.data.db.WidgetTable
import com.glanci.personalization.data.model.WidgetDataModel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class WidgetRepositoryImpl(
    private val database: Database
) : WidgetRepository {

    constructor(databaseProvider: GlanciDatabaseProvider) : this(database = databaseProvider.database)


    override fun upsertWidgets(widgets: List<WidgetDataModel>) {
        transaction(database) {
            WidgetTable.batchUpsert(widgets) { widget ->
                this[WidgetTable.userId] = widget.userId
                this[WidgetTable.name] = widget.name
                this[WidgetTable.orderNum] = widget.orderNum
                this[WidgetTable.timestamp] = widget.timestamp
                this[WidgetTable.deleted] = widget.deleted
            }
        }
    }

    override fun getWidgetsAfterTimestamp(
        userId: Int,
        timestamp: Long
    ): List<WidgetDataModel> {
        return transaction(database) {
            WidgetTable
                .selectAll()
                .where {
                    (WidgetTable.userId eq userId) and (WidgetTable.timestamp greater timestamp)
                }
                .map {
                    WidgetDataModel(
                        userId = it[WidgetTable.userId],
                        name = it[WidgetTable.name],
                        orderNum = it[WidgetTable.orderNum],
                        timestamp = it[WidgetTable.timestamp],
                        deleted = it[WidgetTable.deleted]
                    )
                }
        }
    }

}