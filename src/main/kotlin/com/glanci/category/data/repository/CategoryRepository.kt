package com.glanci.category.data.repository

import com.glanci.category.data.model.CategoryDataModel

interface CategoryRepository {

    fun upsertCategories(categories: List<CategoryDataModel>)

    fun getCategoriesAfterTimestamp(userId: Int, timestamp: Long): List<CategoryDataModel>

}