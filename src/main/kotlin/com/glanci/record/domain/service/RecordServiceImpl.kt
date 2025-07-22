package com.glanci.record.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.domain.dto.TableName
import com.glanci.core.error.UpdateTimeException
import com.glanci.record.data.repository.RecordRepository
import com.glanci.record.error.RecordError
import com.glanci.record.mapper.toDataModel
import com.glanci.record.mapper.toQueryDto
import com.glanci.record.shared.dto.RecordWithItemsCommandDto
import com.glanci.record.shared.dto.RecordWithItemsQueryDto
import com.glanci.record.shared.service.RecordService

class RecordServiceImpl(
    private val recordRepository: RecordRepository,
    private val updateTimeRepository: UpdateTimeRepository
) : RecordService {

    private val tableName = TableName.Record


    override suspend fun getUpdateTime(token: String): Long? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            updateTimeRepository.getUpdateTime(userId = user.id, tableName = tableName) ?: 0
        }
            .onFailure { throw UpdateTimeException.UpdateTimeNotFetched() }
            .getOrNull()
    }

    private fun saveUpdateTime(timestamp: Long, userId: Int) {
        runCatching {
            updateTimeRepository.saveUpdateTime(userId = userId, tableName = tableName, timestamp = timestamp)
        }
            .onFailure { throw UpdateTimeException.UpdateTimeNotSaved() }
    }

    override suspend fun saveRecordsWithItems(
        recordsWithItems: List<RecordWithItemsCommandDto>,
        timestamp: Long,
        token: String
    ) {
        val user = authorizeAtLeastAsUser(token = token)

        runCatching {
            recordRepository.upsertRecordsWithItems(
                recordsWithItems = recordsWithItems.map { it.toDataModel(userId = user.id) }
            )
        }
            .onFailure { throw RecordError.RecordsWithItemsNotSaved() }

        saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getRecordsWithItemsAfterTimestamp(
        timestamp: Long,
        token: String
    ): List<RecordWithItemsQueryDto>? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            recordRepository.getRecordsWithItemsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toQueryDto() }
        }
            .onFailure { throw RecordError.RecordsWithItemsNotFetched() }
            .getOrNull()
    }

    override suspend fun saveRecordsWithItemsAndGetAfterTimestamp(
        recordsWithItems: List<RecordWithItemsCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): List<RecordWithItemsQueryDto>? {
        saveRecordsWithItems(recordsWithItems = recordsWithItems, timestamp = timestamp, token = token)
        return getRecordsWithItemsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}