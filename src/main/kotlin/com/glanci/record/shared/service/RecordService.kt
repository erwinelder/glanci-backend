package com.glanci.record.shared.service

import com.glanci.record.shared.dto.RecordWithItemsCommandDto
import com.glanci.record.shared.dto.RecordWithItemsQueryDto
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.RootError
import kotlinx.rpc.annotations.Rpc

@Rpc
interface RecordService {

    suspend fun getUpdateTime(token: String): ResultData<Long, RootError>

    suspend fun saveRecordsWithItems(
        recordsWithItems: List<RecordWithItemsCommandDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<RootError>

    suspend fun getRecordsWithItemsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<RecordWithItemsQueryDto>, RootError>

    suspend fun saveRecordsWithItemsAndGetAfterTimestamp(
        recordsWithItems: List<RecordWithItemsCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<RecordWithItemsQueryDto>, RootError>

}