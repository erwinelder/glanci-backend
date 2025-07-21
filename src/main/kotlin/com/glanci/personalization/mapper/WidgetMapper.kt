package com.glanci.personalization.mapper

import com.glanci.personalization.data.model.WidgetDataModel
import com.glanci.personalization.shared.dto.WidgetDto


fun WidgetDto.toDataModel(userId: Int): WidgetDataModel {
    return WidgetDataModel(
        userId = userId,
        name = name,
        orderNum = orderNum,
        timestamp = timestamp,
        deleted = deleted
    )
}


fun WidgetDataModel.toDto(): WidgetDto {
    return WidgetDto(
        name = name,
        orderNum = orderNum,
        timestamp = timestamp,
        deleted = deleted
    )
}
