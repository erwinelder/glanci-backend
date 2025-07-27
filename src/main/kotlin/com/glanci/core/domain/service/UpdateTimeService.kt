package com.glanci.core.domain.service

import com.glanci.request.shared.error.UpdateTimeDataError
import com.glanci.request.shared.ResultData
import com.glanci.request.shared.SimpleResult

interface UpdateTimeService {

    fun getUpdateTime(userId: Int): ResultData<Long, UpdateTimeDataError>

    fun saveUpdateTime(timestamp: Long, userId: Int): SimpleResult<UpdateTimeDataError>

}