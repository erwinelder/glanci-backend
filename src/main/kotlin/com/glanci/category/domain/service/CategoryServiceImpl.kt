package com.glanci.category.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUserResult
import com.glanci.category.data.repository.CategoryRepository
import com.glanci.category.mapper.toDataModel
import com.glanci.category.mapper.toQueryDto
import com.glanci.category.shared.dto.CategoryCommandDto
import com.glanci.category.shared.dto.CategoryQueryDto
import com.glanci.category.shared.service.CategoryService
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.CategoryError
import com.glanci.request.domain.error.DataError
import com.glanci.request.domain.getDataOrReturn
import com.glanci.request.domain.returnIfError

class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val updateTimeService: UpdateTimeService
): CategoryService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveCategories(
        categories: List<CategoryCommandDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        runCatching {
            categoryRepository.upsertCategories(
                categories = categories.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(CategoryError.CategoriesNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getCategoriesAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<CategoryQueryDto>, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }

        val categories = runCatching {
            categoryRepository.getCategoriesAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toQueryDto() }
        }.getOrElse {
            return ResultData.Error(CategoryError.CategoriesNotFetched)
        }

        return ResultData.Success(data = categories)
    }

    override suspend fun saveCategoriesAndGetAfterTimestamp(
        categories: List<CategoryCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<CategoryQueryDto>, DataError> {
        saveCategories(categories = categories, timestamp = timestamp, token = token)
            .returnIfError { return ResultData.Error(it) }
        return getCategoriesAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}