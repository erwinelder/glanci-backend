package com.glanci.record.data.repository

import com.glanci.record.data.model.RecordWithItemsDataModel

interface RecordRepository {

    fun upsertRecordsWithItems(recordsWithItems: List<RecordWithItemsDataModel>)

    fun getRecordsWithItemsAfterTimestamp(userId: Int, timestamp: Long): List<RecordWithItemsDataModel>

}