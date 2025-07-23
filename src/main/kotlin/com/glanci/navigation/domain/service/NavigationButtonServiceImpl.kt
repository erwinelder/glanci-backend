package com.glanci.navigation.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUserResult
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.navigation.data.repository.NavigationButtonRepository
import com.glanci.navigation.mapper.toDataModel
import com.glanci.navigation.mapper.toDto
import com.glanci.navigation.shared.dto.NavigationButtonDto
import com.glanci.navigation.shared.service.NavigationButtonService
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.NavigationButtonError
import com.glanci.request.domain.error.RootError
import com.glanci.request.domain.getDataOrReturn
import com.glanci.request.domain.returnIfError

class NavigationButtonServiceImpl(
    private val navigationButtonRepository: NavigationButtonRepository,
    private val updateTimeService: UpdateTimeService
) : NavigationButtonService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, RootError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveNavigationButtons(
        buttons: List<NavigationButtonDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<RootError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        runCatching {
            navigationButtonRepository.upsertNavigationButtons(
                buttons = buttons.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(NavigationButtonError.NavigationButtonsNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getNavigationButtonsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<NavigationButtonDto>, RootError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }

        val accounts = runCatching {
            navigationButtonRepository.getNavigationButtonsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }.getOrElse {
            return ResultData.Error(NavigationButtonError.NavigationButtonsNotFetched)
        }

        return ResultData.Success(data = accounts)
    }

    override suspend fun saveNavigationButtonsAndGetAfterTimestamp(
        buttons: List<NavigationButtonDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<NavigationButtonDto>, RootError> {
        saveNavigationButtons(buttons = buttons, timestamp = timestamp, token = token)
            .returnIfError { return ResultData.Error(it) }
        return getNavigationButtonsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}