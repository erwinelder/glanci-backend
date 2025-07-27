package com.glanci.personalization.shared.service

import com.glanci.personalization.shared.dto.WidgetDto
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.DataError
import kotlinx.rpc.annotations.Rpc

@Rpc
interface WidgetService {

    suspend fun getUpdateTime(token: String): ResultData<Long, DataError>

    suspend fun saveWidgets(widgets: List<WidgetDto>, timestamp: Long, token: String): SimpleResult<DataError>

    suspend fun getWidgetsAfterTimestamp(timestamp: Long, token: String): ResultData<List<WidgetDto>, DataError>

    suspend fun saveWidgetsAndGetAfterTimestamp(
        widgets: List<WidgetDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<WidgetDto>, DataError>

}