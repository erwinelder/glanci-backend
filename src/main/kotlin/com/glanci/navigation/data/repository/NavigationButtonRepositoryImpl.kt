package com.glanci.navigation.data.repository

import com.glanci.core.data.db.GlanciDatabaseProvider
import com.glanci.navigation.data.db.NavigationButtonTable
import com.glanci.navigation.data.model.NavigationButtonDataModel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class NavigationButtonRepositoryImpl(
    private val database: Database
) : NavigationButtonRepository {

    constructor(databaseProvider: GlanciDatabaseProvider) : this(database = databaseProvider.database)


    override fun upsertNavigationButtons(buttons: List<NavigationButtonDataModel>) {
        transaction(database) {
            NavigationButtonTable.batchUpsert(buttons) { button ->
                this[NavigationButtonTable.userId] = button.userId
                this[NavigationButtonTable.screenName] = button.screenName
                this[NavigationButtonTable.orderNum] = button.orderNum
                this[NavigationButtonTable.timestamp] = button.timestamp
                this[NavigationButtonTable.deleted] = button.deleted
            }
        }
    }

    override fun getNavigationButtonsAfterTimestamp(
        userId: Int,
        timestamp: Long
    ): List<NavigationButtonDataModel> {
        return transaction(database) {
            NavigationButtonTable
                .selectAll()
                .where {
                    (NavigationButtonTable.userId eq userId) and (NavigationButtonTable.timestamp greater timestamp)
                }
                .map {
                    NavigationButtonDataModel(
                        userId = it[NavigationButtonTable.userId],
                        screenName = it[NavigationButtonTable.screenName],
                        orderNum = it[NavigationButtonTable.orderNum],
                        timestamp = it[NavigationButtonTable.timestamp],
                        deleted = it[NavigationButtonTable.deleted]
                    )
                }
        }
    }

}