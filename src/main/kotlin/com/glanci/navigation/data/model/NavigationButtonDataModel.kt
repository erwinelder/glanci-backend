package com.glanci.navigation.data.model

data class NavigationButtonDataModel(
    val userId: Int,
    val screenName: String,
    val orderNum: Int,
    val timestamp: Long,
    val deleted: Boolean
)