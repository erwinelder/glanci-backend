package com.glanci.record.data.model

data class RecordWithItemsDataModel(
    val record: RecordDataModel,
    val items: List<RecordItemDataModel>
) {

    val deleted: Boolean
        get() = record.deleted


    fun asPair(): Pair<RecordDataModel, List<RecordItemDataModel>> {
        return record to items.takeUnless { record.deleted }.orEmpty()
    }

}
