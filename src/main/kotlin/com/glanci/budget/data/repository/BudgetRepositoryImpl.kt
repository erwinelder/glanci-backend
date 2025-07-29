package com.glanci.budget.data.repository

import com.glanci.budget.data.db.BudgetAccountAssociationTable
import com.glanci.budget.data.db.BudgetTable
import com.glanci.budget.data.model.BudgetAccountAssociationDataModel
import com.glanci.budget.data.model.BudgetDataModel
import com.glanci.budget.data.model.BudgetWithAssociationsDataModel
import com.glanci.budget.data.utils.divide
import com.glanci.budget.data.utils.zipWithAssociations
import com.glanci.core.data.db.GlanciDatabaseProvider
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction

class BudgetRepositoryImpl(
    private val database: Database
) : BudgetRepository {

    constructor(databaseProvider: GlanciDatabaseProvider) : this(database = databaseProvider.database)


    override fun upsertBudgetsWithAssociations(
        userId: Int,
        budgetsWithAssociations: List<BudgetWithAssociationsDataModel>
    ) {
        val (budgets, associations) = budgetsWithAssociations.divide()
        val budgetIdsToDelete = budgets.mapNotNull { budget -> budget.takeIf { it.deleted }?.id }

        transaction(database) {
            BudgetTable.batchUpsert(budgets) { budget ->
                this[BudgetTable.userId] = budget.userId
                this[BudgetTable.id] = budget.id
                this[BudgetTable.amountLimit] = budget.amountLimit
                this[BudgetTable.categoryId] = budget.categoryId
                this[BudgetTable.name] = budget.name
                this[BudgetTable.repeatingPeriod] = budget.repeatingPeriod
                this[BudgetTable.timestamp] = budget.timestamp
                this[BudgetTable.deleted] = budget.deleted
            }
            BudgetAccountAssociationTable.batchUpsert(associations) { association ->
                this[BudgetAccountAssociationTable.userId] = association.userId
                this[BudgetAccountAssociationTable.budgetId] = association.budgetId
                this[BudgetAccountAssociationTable.accountId] = association.accountId
            }
            BudgetAccountAssociationTable.deleteWhere {
                (BudgetAccountAssociationTable.userId eq userId) and
                        (BudgetAccountAssociationTable.budgetId inList budgetIdsToDelete)
            }
        }
    }

    private fun getBudgetsAfterTimestamp(userId: Int, timestamp: Long): List<BudgetDataModel> {
        return transaction(database) {
            BudgetTable
                .selectAll()
                .where {
                    (BudgetTable.userId eq userId) and (BudgetTable.timestamp greater timestamp)
                }
                .map {
                    BudgetDataModel(
                        userId = it[BudgetTable.userId],
                        id = it[BudgetTable.id],
                        amountLimit = it[BudgetTable.amountLimit],
                        categoryId = it[BudgetTable.categoryId],
                        name = it[BudgetTable.name],
                        repeatingPeriod = it[BudgetTable.repeatingPeriod],
                        timestamp = it[BudgetTable.timestamp],
                        deleted = it[BudgetTable.deleted]
                    )
                }
        }
    }

    private fun getAssociationsByBudgetIds(
        userId: Int,
        budgetIds: List<Int>
    ): List<BudgetAccountAssociationDataModel> {
        return transaction(database) {
            BudgetAccountAssociationTable
                .selectAll()
                .where {
                    (BudgetAccountAssociationTable.userId eq userId) and
                            (BudgetAccountAssociationTable.budgetId inList budgetIds)
                }
                .map {
                    BudgetAccountAssociationDataModel(
                        userId = it[BudgetAccountAssociationTable.userId],
                        budgetId = it[BudgetAccountAssociationTable.budgetId],
                        accountId = it[BudgetAccountAssociationTable.accountId]
                    )
                }
        }
    }

    override fun getBudgetsWithAssociationsAfterTimestamp(
        userId: Int,
        timestamp: Long
    ): List<BudgetWithAssociationsDataModel> {
        val budgets = getBudgetsAfterTimestamp(userId = userId, timestamp = timestamp)
        val ids = budgets.map { it.id }
        val associations = getAssociationsByBudgetIds(userId = userId, budgetIds = ids)

        return budgets.zipWithAssociations(associations = associations)
    }

}