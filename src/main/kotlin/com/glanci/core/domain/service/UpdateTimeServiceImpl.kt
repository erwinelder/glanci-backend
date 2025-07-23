package com.glanci.core.domain.service

import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.domain.model.app.TableName
import com.glanci.request.domain.error.UpdateTimeError
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult

class UpdateTimeServiceImpl(
    private val updateTimeRepository: UpdateTimeRepository,
    private val tableName: TableName
) : UpdateTimeService {

    override fun getUpdateTime(userId: Int): ResultData<Long, UpdateTimeError> {
        val updateTime = runCatching {
            updateTimeRepository.getUpdateTime(userId = userId, tableName = tableName) ?: 0
        }.getOrElse {
            return ResultData.Error(UpdateTimeError.UpdateTimeNotFetched)
        }

        return ResultData.Success(data = updateTime)
    }

    override fun saveUpdateTime(
        timestamp: Long,
        userId: Int
    ): SimpleResult<UpdateTimeError> {
        runCatching {
            updateTimeRepository.saveUpdateTime(userId = userId, tableName = tableName, timestamp = timestamp)
        }.onFailure {
            return SimpleResult.Error(UpdateTimeError.UpdateTimeNotSaved)
        }

        return SimpleResult.Success()
    }

}