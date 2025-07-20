package com.glanci.categoryCollection.data.model

data class CategoryCollectionDataModel(
    val userId: Int,
    val id: Int,
    val orderNum: Int,
    val type: Char,
    val name: String,
    val timestamp: Long,
    val deleted: Boolean
)
