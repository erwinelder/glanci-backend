package com.glanci.record.data.utils

import com.glanci.record.data.model.RecordDataModel
import com.glanci.record.data.model.RecordItemDataModel
import com.glanci.record.data.model.RecordWithItemsDataModel


fun List<RecordWithItemsDataModel>.divide(
): Pair<List<RecordDataModel>, List<RecordItemDataModel>> {
    return map { it.asPair() }
        .unzip()
        .let { it.first to it.second.flatten() }
}

fun List<RecordDataModel>.zipWithItems(items: List<RecordItemDataModel>): List<RecordWithItemsDataModel> {
    val items = items.groupBy { it.recordId }

    return mapNotNull { record ->
        RecordWithItemsDataModel(
            record = record,
            items = items[record.id] ?: return@mapNotNull null
        )
    }
}
