package com.glanci.categoryCollection.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.categoryCollection.data.repository.CategoryCollectionRepository
import com.glanci.categoryCollection.error.CategoryCollectionError
import com.glanci.categoryCollection.mapper.toDataModel
import com.glanci.categoryCollection.mapper.toDto
import com.glanci.categoryCollection.shared.dto.CategoryCollectionWithAssociationsDto
import com.glanci.categoryCollection.shared.service.CategoryCollectionService
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.domain.dto.TableName
import com.glanci.core.error.UpdateTimeException

class CategoryCollectionServiceImpl(
    private val categoryCollectionRepository: CategoryCollectionRepository,
    private val updateTimeRepository: UpdateTimeRepository
) : CategoryCollectionService {

    private val tableName = TableName.CategoryCollection


    override suspend fun getUpdateTime(token: String): Long? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            updateTimeRepository.getUpdateTime(userId = user.id, tableName = tableName) ?: 0
        }
            .onFailure { throw UpdateTimeException.UpdateTimeNotFetched() }
            .getOrNull()
    }

    private fun saveUpdateTime(timestamp: Long, userId: Int) {
        runCatching {
            updateTimeRepository.saveUpdateTime(userId = userId, tableName = tableName, timestamp = timestamp)
        }
            .onFailure { throw UpdateTimeException.UpdateTimeNotSaved() }
    }

    override suspend fun saveCategoryCollectionsWithAssociations(
        collections: List<CategoryCollectionWithAssociationsDto>,
        timestamp: Long,
        token: String
    ) {
        val user = authorizeAtLeastAsUser(token = token)

        runCatching {
            categoryCollectionRepository.upsertCategoryCollectionsWithAssociations(
                collections = collections.map { it.toDataModel(userId = user.id) }
            )
        }
            .onFailure { throw CategoryCollectionError.CategoryCollectionsNotSaved() }

        saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getCategoryCollectionsWithAssociationsAfterTimestamp(
        timestamp: Long,
        token: String
    ): List<CategoryCollectionWithAssociationsDto>? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            categoryCollectionRepository
                .getCategoryCollectionsWithAssociationsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }
            .onFailure { throw CategoryCollectionError.CategoryCollectionsNotFetched() }
            .getOrNull()
    }

    override suspend fun saveCategoryCollectionsWithAssociationsAndGetAfterTimestamp(
        collections: List<CategoryCollectionWithAssociationsDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): List<CategoryCollectionWithAssociationsDto>? {
        saveCategoryCollectionsWithAssociations(collections = collections, timestamp = timestamp, token = token)
        return getCategoryCollectionsWithAssociationsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}