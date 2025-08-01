package com.glanci.category.shared.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryCommandDto(
    val id: Int,
    val type: Char,
    val orderNum: Int,
    val parentCategoryId: Int?,
    val name: String,
    val iconName: String,
    val colorName: String,
    val timestamp: Long,
    val deleted: Boolean
)
