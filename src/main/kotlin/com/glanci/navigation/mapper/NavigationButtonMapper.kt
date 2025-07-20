package com.glanci.navigation.mapper

import com.glanci.navigation.data.model.NavigationButtonDataModel
import com.glanci.navigation.shared.dto.NavigationButtonDto


fun NavigationButtonDto.toDataModel(userId: Int): NavigationButtonDataModel {
    return NavigationButtonDataModel(
        userId = userId,
        screenName = screenName,
        orderNum = orderNum,
        timestamp = timestamp,
        deleted = deleted
    )
}


fun NavigationButtonDataModel.toDto(): NavigationButtonDto {
    return NavigationButtonDto(
        screenName = screenName,
        orderNum = orderNum,
        timestamp = timestamp,
        deleted = deleted
    )
}
