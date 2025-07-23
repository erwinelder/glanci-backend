package com.glanci.categoryCollection.shared.service

import com.glanci.categoryCollection.shared.dto.CategoryCollectionWithAssociationsDto
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.RootError
import kotlinx.rpc.annotations.Rpc

@Rpc
interface CategoryCollectionService {

    suspend fun getUpdateTime(token: String): ResultData<Long, RootError>

    suspend fun saveCategoryCollectionsWithAssociations(
        collections: List<CategoryCollectionWithAssociationsDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<RootError>

    suspend fun getCategoryCollectionsWithAssociationsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<CategoryCollectionWithAssociationsDto>, RootError>

    suspend fun saveCategoryCollectionsWithAssociationsAndGetAfterTimestamp(
        collections: List<CategoryCollectionWithAssociationsDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<CategoryCollectionWithAssociationsDto>, RootError>

}