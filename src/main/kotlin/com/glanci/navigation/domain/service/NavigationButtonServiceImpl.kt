package com.glanci.navigation.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.navigation.data.repository.NavigationButtonRepository
import com.glanci.navigation.mapper.toDataModel
import com.glanci.navigation.mapper.toDto
import com.glanci.navigation.shared.dto.NavigationButtonDto
import com.glanci.navigation.shared.service.NavigationButtonService
import com.glanci.request.shared.ResultData
import com.glanci.request.shared.SimpleResult
import com.glanci.request.shared.getOrElse
import com.glanci.request.shared.onError
import com.glanci.request.shared.error.DataError
import com.glanci.request.shared.error.NavigationButtonDataError

class NavigationButtonServiceImpl(
    private val navigationButtonRepository: NavigationButtonRepository,
    private val updateTimeService: UpdateTimeService
) : NavigationButtonService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, DataError> {
        val user = authorizeAtLeastAsUser(token = token).getOrElse { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveNavigationButtons(
        buttons: List<NavigationButtonDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<DataError> {
        val user = authorizeAtLeastAsUser(token = token).getOrElse { return SimpleResult.Error(it) }

        runCatching {
            navigationButtonRepository.upsertNavigationButtons(
                buttons = buttons.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(NavigationButtonDataError.NavigationButtonsNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getNavigationButtonsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<NavigationButtonDto>, DataError> {
        val user = authorizeAtLeastAsUser(token = token).getOrElse { return ResultData.Error(it) }

        val accounts = runCatching {
            navigationButtonRepository.getNavigationButtonsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }.getOrElse {
            return ResultData.Error(NavigationButtonDataError.NavigationButtonsNotFetched)
        }

        return ResultData.Success(data = accounts)
    }

    override suspend fun saveNavigationButtonsAndGetAfterTimestamp(
        buttons: List<NavigationButtonDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<NavigationButtonDto>, DataError> {
        saveNavigationButtons(buttons = buttons, timestamp = timestamp, token = token)
            .onError { return ResultData.Error(it) }
        return getNavigationButtonsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}