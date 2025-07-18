package com.glanci.category.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.category.data.repository.CategoryRepository
import com.glanci.category.error.CategoryError
import com.glanci.category.mapper.toDataModel
import com.glanci.category.mapper.toQueryDto
import com.glanci.category.domain.dto.CategoryCommandDto
import com.glanci.category.domain.dto.CategoryQueryDto
import com.glanci.category.shared.service.CategoryService
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.domain.dto.TableName
import com.glanci.core.error.UpdateTimeError

class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val updateTimeRepository: UpdateTimeRepository
): CategoryService {

    private val tableName = TableName.Category


    override suspend fun getUpdateTime(token: String): Long? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            updateTimeRepository.getUpdateTime(userId = user.id, tableName = tableName) ?: 0
        }
            .onFailure { throw UpdateTimeError.UpdateTimeNotFetched() }
            .getOrNull()
    }

    private fun saveUpdateTime(timestamp: Long, userId: Int) {
        runCatching {
            updateTimeRepository.saveUpdateTime(userId = userId, tableName = tableName, timestamp = timestamp)
        }
            .onFailure { throw UpdateTimeError.UpdateTimeNotSaved() }
    }

    override suspend fun saveCategories(categories: List<CategoryCommandDto>, timestamp: Long, token: String) {
        val user = authorizeAtLeastAsUser(token = token)

        runCatching {
            categoryRepository.upsertCategories(
                categories = categories.map { it.toDataModel(userId = user.id) }
            )
        }
            .onFailure { throw CategoryError.CategoriesNotSaved() }

        saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getCategoriesAfterTimestamp(
        timestamp: Long,
        token: String
    ): List<CategoryQueryDto>? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            categoryRepository.getCategoriesAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toQueryDto() }
        }
            .onFailure { throw CategoryError.CategoriesNotFetched() }
            .getOrNull()
    }

    override suspend fun saveCategoriesAndGetAfterTimestamp(
        categories: List<CategoryCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): List<CategoryQueryDto>? {
        saveCategories(categories = categories, timestamp = timestamp, token = token)
        return getCategoriesAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}