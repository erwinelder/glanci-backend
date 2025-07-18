package com.glanci.category.mapper

import com.glanci.category.data.model.CategoryDataModel
import com.glanci.category.domain.dto.CategoryCommandDto
import com.glanci.category.domain.dto.CategoryQueryDto


fun CategoryCommandDto.toDataModel(userId: Int): CategoryDataModel {
    return CategoryDataModel(
        userId = userId,
        id = id,
        type = type,
        orderNum = orderNum,
        parentCategoryId = parentCategoryId,
        name = name,
        iconName = iconName,
        colorName = colorName,
        timestamp = timestamp,
        deleted = deleted
    )
}

fun CategoryDataModel.toQueryDto(): CategoryQueryDto {
    return CategoryQueryDto(
        userId = userId,
        id = id,
        type = type,
        orderNum = orderNum,
        parentCategoryId = parentCategoryId,
        name = name,
        iconName = iconName,
        colorName = colorName,
        timestamp = timestamp,
        deleted = deleted
    )
}
