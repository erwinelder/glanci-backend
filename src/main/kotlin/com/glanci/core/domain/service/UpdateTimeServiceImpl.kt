package com.glanci.core.domain.service

import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.domain.model.app.TableName
import com.glanci.request.shared.error.UpdateTimeDataError
import com.glanci.request.shared.ResultData
import com.glanci.request.shared.SimpleResult

class UpdateTimeServiceImpl(
    private val updateTimeRepository: UpdateTimeRepository,
    private val tableName: TableName
) : UpdateTimeService {

    override fun getUpdateTime(userId: Int): ResultData<Long, UpdateTimeDataError> {
        val updateTime = runCatching {
            updateTimeRepository.getUpdateTime(userId = userId, tableName = tableName) ?: 0
        }.getOrElse {
            return ResultData.Error(UpdateTimeDataError.UpdateTimeNotFetched)
        }

        return ResultData.Success(data = updateTime)
    }

    override fun saveUpdateTime(
        timestamp: Long,
        userId: Int
    ): SimpleResult<UpdateTimeDataError> {
        runCatching {
            updateTimeRepository.saveUpdateTime(userId = userId, tableName = tableName, timestamp = timestamp)
        }.onFailure {
            return SimpleResult.Error(UpdateTimeDataError.UpdateTimeNotSaved)
        }

        return SimpleResult.Success()
    }

}