package com.glanci.account.data.model

data class AccountDataModel(
    val userId: Int,
    val id: Int,
    val orderNum: Int,
    val name: String,
    val currency: String,
    val balance: Double,
    val color: String,
    val hide: Boolean,
    val hideBalance: Boolean,
    val withoutBalance: Boolean,
    val timestamp: Long,
    val deleted: Boolean
)