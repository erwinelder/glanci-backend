package com.glanci.personalization.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUserResult
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.personalization.data.repository.WidgetRepository
import com.glanci.personalization.mapper.toDataModel
import com.glanci.personalization.mapper.toDto
import com.glanci.personalization.shared.dto.WidgetDto
import com.glanci.personalization.shared.service.WidgetService
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.RootError
import com.glanci.request.domain.error.WidgetError
import com.glanci.request.domain.getDataOrReturn
import com.glanci.request.domain.returnIfError

class WidgetServiceImpl(
    private val widgetRepository: WidgetRepository,
    private val updateTimeService: UpdateTimeService
) : WidgetService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, RootError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveWidgets(
        widgets: List<WidgetDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<RootError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        runCatching {
            widgetRepository.upsertWidgets(
                widgets = widgets.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(WidgetError.WidgetsNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getWidgetsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<WidgetDto>, RootError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }

        val accounts = runCatching {
            widgetRepository.getWidgetsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }.getOrElse {
            return ResultData.Error(WidgetError.WidgetsNotFetched)
        }

        return ResultData.Success(data = accounts)
    }

    override suspend fun saveWidgetsAndGetAfterTimestamp(
        widgets: List<WidgetDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<WidgetDto>, RootError> {
        saveWidgets(widgets = widgets, timestamp = timestamp, token = token)
            .returnIfError { return ResultData.Error(it) }
        return getWidgetsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}