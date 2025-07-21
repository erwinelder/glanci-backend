package com.glanci.record.data.model

data class RecordDataModel(
    val userId: Int,
    val id: Long,
    val date: Long,
    val type: Char,
    val accountId: Int,
    val includeInBudgets: Boolean,
    val timestamp: Long,
    val deleted: Boolean
)
