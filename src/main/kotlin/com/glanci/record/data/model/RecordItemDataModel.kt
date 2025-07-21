package com.glanci.record.data.model

data class RecordItemDataModel(
    val userId: Int,
    val id: Long,
    val recordId: Long,
    val totalAmount: Double,
    val quantity: Int?,
    val categoryId: Int,
    val subcategoryId: Int?,
    val note: String?
)
