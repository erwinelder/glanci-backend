package com.glanci.record.shared.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecordWithItemsCommandDto(
    val record: RecordCommandDto,
    val items: List<RecordItemDto>
) {

    val deleted: Boolean
        get() = record.deleted

}
