package com.glanci.personalization.data.repository

import com.glanci.personalization.data.model.WidgetDataModel

interface WidgetRepository {

    fun upsertWidgets(widgets: List<WidgetDataModel>)

    fun getWidgetsAfterTimestamp(userId: Int, timestamp: Long): List<WidgetDataModel>

}