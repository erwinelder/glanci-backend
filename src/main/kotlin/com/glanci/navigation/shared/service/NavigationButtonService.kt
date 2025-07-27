package com.glanci.navigation.shared.service

import com.glanci.navigation.shared.dto.NavigationButtonDto
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.DataError
import kotlinx.rpc.annotations.Rpc

@Rpc
interface NavigationButtonService {

    suspend fun getUpdateTime(token: String): ResultData<Long, DataError>

    suspend fun saveNavigationButtons(
        buttons: List<NavigationButtonDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<DataError>

    suspend fun getNavigationButtonsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<NavigationButtonDto>, DataError>

    suspend fun saveNavigationButtonsAndGetAfterTimestamp(
        buttons: List<NavigationButtonDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<NavigationButtonDto>, DataError>

}