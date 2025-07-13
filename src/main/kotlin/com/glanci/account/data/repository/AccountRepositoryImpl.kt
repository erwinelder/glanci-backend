package com.glanci.account.data.repository

import com.glanci.account.data.db.AccountTable
import com.glanci.account.data.model.AccountDataModel
import com.glanci.core.data.db.GlanciDatabaseProvider
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class AccountRepositoryImpl(
    private val database: Database
) : AccountRepository {

    constructor(databaseProvider: GlanciDatabaseProvider) : this(database = databaseProvider.database)


    override fun upsertAccounts(accounts: List<AccountDataModel>) {
        transaction(database) {
            AccountTable.batchUpsert(accounts) { account ->
                this[AccountTable.userId] = account.userId
                this[AccountTable.id] = account.id
                this[AccountTable.orderNum] = account.orderNum
                this[AccountTable.name] = account.name
                this[AccountTable.currency] = account.currency
                this[AccountTable.balance] = account.balance
                this[AccountTable.color] = account.color
                this[AccountTable.hide] = account.hide
                this[AccountTable.hideBalance] = account.hideBalance
                this[AccountTable.withoutBalance] = account.withoutBalance
                this[AccountTable.timestamp] = account.timestamp
                this[AccountTable.deleted] = account.deleted
            }
        }
    }

    override fun getAccountsAfterTimestamp(userId: Int, timestamp: Long): List<AccountDataModel> {
        return transaction(database) {
            AccountTable
                .selectAll()
                .where {
                    (AccountTable.userId eq userId) and (AccountTable.timestamp greater timestamp)
                }
                .map {
                    AccountDataModel(
                        userId = it[AccountTable.userId],
                        id = it[AccountTable.id],
                        orderNum = it[AccountTable.orderNum],
                        name = it[AccountTable.name],
                        currency = it[AccountTable.currency],
                        balance = it[AccountTable.balance],
                        color = it[AccountTable.color],
                        hide = it[AccountTable.hide],
                        hideBalance = it[AccountTable.hideBalance],
                        withoutBalance = it[AccountTable.withoutBalance],
                        timestamp = it[AccountTable.timestamp],
                        deleted = it[AccountTable.deleted]
                    )
                }
        }
    }

}