package com.glanci.personalization.shared.service

import com.glanci.personalization.shared.dto.WidgetDto
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.RootError
import kotlinx.rpc.annotations.Rpc

@Rpc
interface WidgetService {

    suspend fun getUpdateTime(token: String): ResultData<Long, RootError>

    suspend fun saveWidgets(widgets: List<WidgetDto>, timestamp: Long, token: String): SimpleResult<RootError>

    suspend fun getWidgetsAfterTimestamp(timestamp: Long, token: String): ResultData<List<WidgetDto>, RootError>

    suspend fun saveWidgetsAndGetAfterTimestamp(
        widgets: List<WidgetDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<WidgetDto>, RootError>

}