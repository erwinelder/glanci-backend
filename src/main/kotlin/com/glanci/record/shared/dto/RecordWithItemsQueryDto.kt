package com.glanci.record.shared.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecordWithItemsQueryDto(
    val record: RecordQueryDto,
    val items: List<RecordItemDto>
) {

    val deleted: Boolean
        get() = record.deleted

}
