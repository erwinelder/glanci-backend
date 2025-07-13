package com.glanci.core.data.repository

import com.glanci.core.data.db.GlanciDatabaseProvider
import com.glanci.core.data.db.UpdateTimeTable
import com.glanci.core.domain.dto.TableName
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

class UpdateTimeRepositoryImpl(
    private val database: Database
) : UpdateTimeRepository {

    constructor(databaseProvider: GlanciDatabaseProvider) : this(database = databaseProvider.database)


    override fun getUpdateTime(userId: Int, tableName: TableName): Long? {
        return transaction(database) {
            UpdateTimeTable
                .selectAll()
                .where {
                    (UpdateTimeTable.userId eq userId) and (UpdateTimeTable.name eq tableName.name)
                }
                .map {
                    it[UpdateTimeTable.timestamp]
                }
                .singleOrNull()
        }
    }

    override fun saveUpdateTime(
        userId: Int,
        tableName: TableName,
        timestamp: Long
    ) {
        transaction(database) {
            UpdateTimeTable.upsert {
                it[UpdateTimeTable.userId] = userId
                it[UpdateTimeTable.name] = tableName.name
                it[UpdateTimeTable.timestamp] = timestamp
            }
        }
    }

}