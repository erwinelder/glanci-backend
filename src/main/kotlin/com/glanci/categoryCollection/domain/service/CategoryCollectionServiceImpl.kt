package com.glanci.categoryCollection.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUserResult
import com.glanci.categoryCollection.data.repository.CategoryCollectionRepository
import com.glanci.categoryCollection.mapper.toDataModel
import com.glanci.categoryCollection.mapper.toDto
import com.glanci.categoryCollection.shared.dto.CategoryCollectionWithAssociationsDto
import com.glanci.categoryCollection.shared.service.CategoryCollectionService
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.CategoryCollectionError
import com.glanci.request.domain.error.DataError
import com.glanci.request.domain.getDataOrReturn
import com.glanci.request.domain.returnIfError

class CategoryCollectionServiceImpl(
    private val categoryCollectionRepository: CategoryCollectionRepository,
    private val updateTimeService: UpdateTimeService
) : CategoryCollectionService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveCategoryCollectionsWithAssociations(
        collections: List<CategoryCollectionWithAssociationsDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        runCatching {
            categoryCollectionRepository.upsertCategoryCollectionsWithAssociations(
                collections = collections.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(CategoryCollectionError.CategoryCollectionsNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getCategoryCollectionsWithAssociationsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<CategoryCollectionWithAssociationsDto>, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }

        val collectionsWithAssociations = runCatching {
            categoryCollectionRepository
                .getCategoryCollectionsWithAssociationsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }.getOrElse {
            return ResultData.Error(CategoryCollectionError.CategoryCollectionsNotFetched)
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
            .returnIfError { return ResultData.Error(it) }
        return getCategoryCollectionsWithAssociationsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}