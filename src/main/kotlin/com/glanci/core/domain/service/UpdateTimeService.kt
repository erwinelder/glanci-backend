package com.glanci.core.domain.service

import com.glanci.request.domain.error.UpdateTimeError
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult

interface UpdateTimeService {

    fun getUpdateTime(userId: Int): ResultData<Long, UpdateTimeError>

    fun saveUpdateTime(timestamp: Long, userId: Int): SimpleResult<UpdateTimeError>

}