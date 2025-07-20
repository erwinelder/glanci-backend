package com.glanci.navigation.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.domain.dto.TableName
import com.glanci.core.error.UpdateTimeError
import com.glanci.navigation.data.repository.NavigationButtonRepository
import com.glanci.navigation.error.NavigationButtonError
import com.glanci.navigation.mapper.toDataModel
import com.glanci.navigation.mapper.toDto
import com.glanci.navigation.shared.dto.NavigationButtonDto
import com.glanci.navigation.shared.service.NavigationButtonService

class NavigationButtonServiceImpl(
    private val navigationButtonRepository: NavigationButtonRepository,
    private val updateTimeRepository: UpdateTimeRepository
) : NavigationButtonService {

    private val tableName = TableName.NavigationButton


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

    override suspend fun saveNavigationButtons(
        buttons: List<NavigationButtonDto>,
        timestamp: Long,
        token: String
    ) {
        val user = authorizeAtLeastAsUser(token = token)

        runCatching {
            navigationButtonRepository.upsertNavigationButtons(
                buttons = buttons.map { it.toDataModel(userId = user.id) }
            )
        }
            .onFailure { throw NavigationButtonError.NavigationButtonsNotSaved() }

        saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getNavigationButtonsAfterTimestamp(
        timestamp: Long,
        token: String
    ): List<NavigationButtonDto>? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            navigationButtonRepository.getNavigationButtonsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toDto() }
        }
            .onFailure { throw NavigationButtonError.NavigationButtonsNotFetched() }
            .getOrNull()
    }

    override suspend fun saveNavigationButtonsAndGetAfterTimestamp(
        buttons: List<NavigationButtonDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): List<NavigationButtonDto>? {
        saveNavigationButtons(buttons = buttons, timestamp = timestamp, token = token)
        return getNavigationButtonsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}