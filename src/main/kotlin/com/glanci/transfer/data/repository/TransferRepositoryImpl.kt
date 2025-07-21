package com.glanci.transfer.data.repository

import com.glanci.core.data.db.GlanciDatabaseProvider
import com.glanci.transfer.data.db.TransferTable
import com.glanci.transfer.data.model.TransferDataModel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class TransferRepositoryImpl(
    private val database: Database
) : TransferRepository {

    constructor(databaseProvider: GlanciDatabaseProvider) : this(database = databaseProvider.database)


    override fun upsertTransfers(transfers: List<TransferDataModel>) {
        transaction(database) {
            TransferTable.batchUpsert(transfers) { transfer ->
                this[TransferTable.userId] = transfer.userId
                this[TransferTable.id] = transfer.id
                this[TransferTable.date] = transfer.date
                this[TransferTable.senderAccountId] = transfer.senderAccountId
                this[TransferTable.receiverAccountId] = transfer.receiverAccountId
                this[TransferTable.senderAmount] = transfer.senderAmount
                this[TransferTable.receiverAmount] = transfer.receiverAmount
                this[TransferTable.senderRate] = transfer.senderRate
                this[TransferTable.receiverRate] = transfer.receiverRate
                this[TransferTable.includeInBudgets] = transfer.includeInBudgets
                this[TransferTable.timestamp] = transfer.timestamp
                this[TransferTable.deleted] = transfer.deleted
            }
        }
    }

    override fun getTransfersAfterTimestamp(
        userId: Int,
        timestamp: Long
    ): List<TransferDataModel> {
        return transaction(database) {
            TransferTable
                .selectAll()
                .where {
                    (TransferTable.userId eq userId) and (TransferTable.timestamp greater timestamp)
                }
                .map {
                    TransferDataModel(
                        userId = it[TransferTable.userId],
                        id = it[TransferTable.id],
                        date = it[TransferTable.date],
                        senderAccountId = it[TransferTable.senderAccountId],
                        receiverAccountId = it[TransferTable.receiverAccountId],
                        senderAmount = it[TransferTable.senderAmount],
                        receiverAmount = it[TransferTable.receiverAmount],
                        senderRate = it[TransferTable.senderRate],
                        receiverRate = it[TransferTable.receiverRate],
                        includeInBudgets = it[TransferTable.includeInBudgets],
                        timestamp = it[TransferTable.timestamp],
                        deleted = it[TransferTable.deleted]
                    )
                }
        }
    }

}