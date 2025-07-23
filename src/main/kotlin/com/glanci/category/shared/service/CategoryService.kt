package com.glanci.category.shared.service

import com.glanci.category.shared.dto.CategoryCommandDto
import com.glanci.category.shared.dto.CategoryQueryDto
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.RootError
import kotlinx.rpc.annotations.Rpc

@Rpc
interface CategoryService {

    suspend fun getUpdateTime(token: String): ResultData<Long, RootError>

    suspend fun saveCategories(
        categories: List<CategoryCommandDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<RootError>

    suspend fun getCategoriesAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<CategoryQueryDto>, RootError>

    suspend fun saveCategoriesAndGetAfterTimestamp(
        categories: List<CategoryCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<CategoryQueryDto>, RootError>

}