package com.glanci.categoryCollection.data.repository

import com.glanci.categoryCollection.data.model.CategoryCollectionWithAssociationsDataModel

interface CategoryCollectionRepository {

    fun upsertCategoryCollectionsWithAssociations(collections: List<CategoryCollectionWithAssociationsDataModel>)

    fun getCategoryCollectionsWithAssociationsAfterTimestamp(
        userId: Int,
        timestamp: Long
    ): List<CategoryCollectionWithAssociationsDataModel>

}