package com.glanci.personalization.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.domain.dto.TableName
import com.glanci.core.error.UpdateTimeError
import com.glanci.personalization.data.repository.WidgetRepository
import com.glanci.personalization.error.WidgetError
import com.glanci.personalization.mapper.toDataModel
import com.glanci.personalization.mapper.toDto
import com.glanci.personalization.shared.dto.WidgetDto
import com.glanci.personalization.shared.service.WidgetService

class WidgetServiceImpl(
    private val widgetRepository: WidgetRepository,
    private val updateTimeRepository: UpdateTimeRepository
) : WidgetService {

    private val tableName = TableName.Widget


    override suspend fun getUpdateTime(token: String): Long? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            updateTimeRepository.getUpdateTime(userId = user.id, tableName = tableName) ?: 0
        }
            .onFailure { throw UpdateTimeError.UpdateTimeNotFetched() }
            .getOrNull()
    }

    private fun saveUpdateTime(timestamp: Long, userId: Int) {
        runCatching {
            updateTimeRepository.saveUpdateTime(userId = userId, tableName = tableName, timestamp = timestamp)
        }
            .onFailure { throw UpdateTimeError.UpdateTimeNotSaved() }
    }

    override suspend fun saveWidgets(
        widgets: List<WidgetDto>,
        timestamp: Long,
        token: String
    ) {
        val user = authorizeAtLeastAsUser(token = token)

        runCatching {
            widgetRepository.upsertWidgets(
                widgets = widgets.map { it.toDataModel(userId = user.id) }
            )
        }
            .onFailure { throw WidgetError.WidgetsNotSaved() }

        saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getWidgetsAfterTimestamp(
        timestamp: Long,
        token: String
    ): List<WidgetDto>? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            widgetRepository.getWidgetsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }
            .onFailure { throw WidgetError.WidgetsNotFetched() }
            .getOrNull()
    }

    override suspend fun saveWidgetsAndGetAfterTimestamp(
        widgets: List<WidgetDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): List<WidgetDto>? {
        saveWidgets(widgets = widgets, timestamp = timestamp, token = token)
        return getWidgetsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}