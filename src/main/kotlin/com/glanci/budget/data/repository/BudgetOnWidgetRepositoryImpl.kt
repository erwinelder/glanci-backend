package com.glanci.budget.data.repository

import com.glanci.budget.data.db.BudgetOnWidgetTable
import com.glanci.budget.data.model.BudgetOnWidgetDataModel
import com.glanci.core.data.db.GlanciDatabaseProvider
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class BudgetOnWidgetRepositoryImpl(
    private val database: Database
) : BudgetOnWidgetRepository {

    constructor(databaseProvider: GlanciDatabaseProvider) : this(database = databaseProvider.database)


    override fun upsertBudgetsOnWidget(budgets: List<BudgetOnWidgetDataModel>) {
        transaction(database) {
            BudgetOnWidgetTable.batchUpsert(budgets) { budget ->
                this[BudgetOnWidgetTable.userId] = budget.userId
                this[BudgetOnWidgetTable.budgetId] = budget.budgetId
                this[BudgetOnWidgetTable.timestamp] = budget.timestamp
                this[BudgetOnWidgetTable.deleted] = budget.deleted
            }
        }
    }

    override fun getBudgetsOnWidgetAfterTimestamp(
        userId: Int,
        timestamp: Long
    ): List<BudgetOnWidgetDataModel> {
        return transaction(database) {
            BudgetOnWidgetTable
                .selectAll()
                .where {
                    (BudgetOnWidgetTable.userId eq userId) and (BudgetOnWidgetTable.timestamp greater timestamp)
                }
                .map {
                    BudgetOnWidgetDataModel(
                        userId = it[BudgetOnWidgetTable.userId],
                        budgetId = it[BudgetOnWidgetTable.budgetId],
                        timestamp = it[BudgetOnWidgetTable.timestamp],
                        deleted = it[BudgetOnWidgetTable.deleted]
                    )
                }
        }
    }

}