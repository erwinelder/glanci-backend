package com.glanci.personalization.data.model

data class WidgetDataModel(
    val userId: Int,
    val name: String,
    val orderNum: Int,
    val timestamp: Long,
    val deleted: Boolean
)
