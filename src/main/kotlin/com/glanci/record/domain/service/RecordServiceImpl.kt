package com.glanci.record.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUserResult
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.record.data.repository.RecordRepository
import com.glanci.record.mapper.toDataModel
import com.glanci.record.mapper.toQueryDto
import com.glanci.record.shared.dto.RecordWithItemsCommandDto
import com.glanci.record.shared.dto.RecordWithItemsQueryDto
import com.glanci.record.shared.service.RecordService
import com.glanci.request.shared.ResultData
import com.glanci.request.shared.SimpleResult
import com.glanci.request.shared.getDataOrReturn
import com.glanci.request.shared.returnIfError
import com.glanci.request.shared.error.DataError
import com.glanci.request.shared.error.RecordDataError

class RecordServiceImpl(
    private val recordRepository: RecordRepository,
    private val updateTimeService: UpdateTimeService
) : RecordService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveRecordsWithItems(
        recordsWithItems: List<RecordWithItemsCommandDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        runCatching {
            recordRepository.upsertRecordsWithItems(
                recordsWithItems = recordsWithItems.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(RecordDataError.RecordsWithItemsNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getRecordsWithItemsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<RecordWithItemsQueryDto>, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }

        val recordsWithItems = runCatching {
            recordRepository.getRecordsWithItemsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toQueryDto() }
        }.getOrElse {
            return ResultData.Error(RecordDataError.RecordsWithItemsNotFetched)
        }

        return ResultData.Success(data = recordsWithItems)
    }

    override suspend fun saveRecordsWithItemsAndGetAfterTimestamp(
        recordsWithItems: List<RecordWithItemsCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<RecordWithItemsQueryDto>, DataError> {
        saveRecordsWithItems(recordsWithItems = recordsWithItems, timestamp = timestamp, token = token)
            .returnIfError { return ResultData.Error(it) }
        return getRecordsWithItemsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}