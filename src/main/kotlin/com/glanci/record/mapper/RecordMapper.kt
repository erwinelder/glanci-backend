package com.glanci.record.mapper

import com.glanci.record.data.model.RecordDataModel
import com.glanci.record.data.model.RecordItemDataModel
import com.glanci.record.data.model.RecordWithItemsDataModel
import com.glanci.record.shared.dto.*


fun RecordCommandDto.toDataModel(userId: Int): RecordDataModel {
    return RecordDataModel(
        userId = userId,
        id = id,
        date = date,
        type = type,
        accountId = accountId,
        includeInBudgets = includeInBudgets,
        timestamp = timestamp,
        deleted = deleted
    )
}

fun RecordItemDto.toDataModel(userId: Int): RecordItemDataModel {
    return RecordItemDataModel(
        userId = userId,
        id = id,
        recordId = recordId,
        totalAmount = totalAmount,
        quantity = quantity,
        categoryId = categoryId,
        subcategoryId = subcategoryId,
        note = note
    )
}

fun RecordWithItemsCommandDto.toDataModel(userId: Int): RecordWithItemsDataModel {
    return RecordWithItemsDataModel(
        record = record.toDataModel(userId = userId),
        items = items.map { it.toDataModel(userId = userId) }
    )
}


fun RecordDataModel.toQueryDto(): RecordQueryDto {
    return RecordQueryDto(
        userId = userId,
        id = id,
        date = date,
        type = type,
        accountId = accountId,
        includeInBudgets = includeInBudgets,
        timestamp = timestamp,
        deleted = deleted
    )
}

fun RecordItemDataModel.toDto(): RecordItemDto {
    return RecordItemDto(
        id = id,
        recordId = recordId,
        totalAmount = totalAmount,
        quantity = quantity,
        categoryId = categoryId,
        subcategoryId = subcategoryId,
        note = note
    )
}

fun RecordWithItemsDataModel.toQueryDto(): RecordWithItemsQueryDto {
    return RecordWithItemsQueryDto(
        record = record.toQueryDto(),
        items = items.map { it.toDto() }
    )
}
