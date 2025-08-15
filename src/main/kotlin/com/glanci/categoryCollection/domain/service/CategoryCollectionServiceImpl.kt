package com.glanci.categoryCollection.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.categoryCollection.data.repository.CategoryCollectionRepository
import com.glanci.categoryCollection.mapper.toDataModel
import com.glanci.categoryCollection.mapper.toDto
import com.glanci.categoryCollection.shared.dto.CategoryCollectionWithAssociationsDto
import com.glanci.categoryCollection.shared.service.CategoryCollectionService
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.request.shared.ResultData
import com.glanci.request.shared.SimpleResult
import com.glanci.request.shared.getOrElse
import com.glanci.request.shared.onError
import com.glanci.request.shared.error.CategoryCollectionDataError
import com.glanci.request.shared.error.DataError

class CategoryCollectionServiceImpl(
    private val categoryCollectionRepository: CategoryCollectionRepository,
    private val updateTimeService: UpdateTimeService
) : CategoryCollectionService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, DataError> {
        val user = authorizeAtLeastAsUser(token = token).getOrElse { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveCategoryCollectionsWithAssociations(
        collections: List<CategoryCollectionWithAssociationsDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<DataError> {
        val user = authorizeAtLeastAsUser(token = token).getOrElse { return SimpleResult.Error(it) }

        runCatching {
            categoryCollectionRepository.upsertCategoryCollectionsWithAssociations(
                userId = user.id,
                collections = collections.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(CategoryCollectionDataError.CategoryCollectionsNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getCategoryCollectionsWithAssociationsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<CategoryCollectionWithAssociationsDto>, DataError> {
        val user = authorizeAtLeastAsUser(token = token).getOrElse { return ResultData.Error(it) }

        val collectionsWithAssociations = runCatching {
            categoryCollectionRepository
                .getCategoryCollectionsWithAssociationsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }.getOrElse {
            return ResultData.Error(CategoryCollectionDataError.CategoryCollectionsNotFetched)
        }

        return ResultData.Success(data = collectionsWithAssociations)
    }

    override suspend fun saveCategoryCollectionsWithAssociationsAndGetAfterTimestamp(
        collections: List<CategoryCollectionWithAssociationsDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<CategoryCollectionWithAssociationsDto>, DataError> {
        saveCategoryCollectionsWithAssociations(collections = collections, timestamp = timestamp, token = token)
            .onError { return ResultData.Error(it) }
        return getCategoryCollectionsWithAssociationsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}