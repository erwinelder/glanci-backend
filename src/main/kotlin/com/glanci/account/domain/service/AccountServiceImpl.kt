package com.glanci.account.domain.service

import com.glanci.account.data.repository.AccountRepository
import com.glanci.account.error.AccountError
import com.glanci.account.mapper.toDataModel
import com.glanci.account.mapper.toQueryDto
import com.glanci.account.shared.model.AccountCommandDto
import com.glanci.account.shared.model.AccountQueryDto
import com.glanci.account.shared.service.AccountService
import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.domain.dto.TableName
import com.glanci.core.error.UpdateTimeError

class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val updateTimeRepository: UpdateTimeRepository
) : AccountService {

    override suspend fun getUpdateTime(token: String): Long? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            updateTimeRepository.getUpdateTime(userId = user.id, tableName = TableName.Account)
        }
            .onFailure { throw UpdateTimeError.UpdateTimeNotFetched() }
            .getOrNull()
    }

    private fun saveUpdateTime(timestamp: Long, userId: Int) {
        runCatching {
            updateTimeRepository.saveUpdateTime(userId = userId, tableName = TableName.Account, timestamp = timestamp)
        }
            .onFailure { throw UpdateTimeError.UpdateTimeNotSaved() }
    }

    override suspend fun saveAccounts(accounts: List<AccountCommandDto>, timestamp: Long, token: String) {
        val user = authorizeAtLeastAsUser(token = token)

        runCatching {
            accountRepository.upsertAccounts(
                accounts = accounts.map { it.toDataModel(userId = user.id) }
            )
            saveUpdateTime(timestamp = timestamp, userId = user.id)
        }
            .onFailure { throw AccountError.AccountsNotSaved() }
    }

    override suspend fun getAccountsAfterTimestamp(timestamp: Long, token: String): List<AccountQueryDto>? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            accountRepository.getAccountsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toQueryDto() }
        }
            .onFailure { throw AccountError.AccountsNotFetched() }
            .getOrNull()
    }

    override suspend fun saveAccountsAndGetAfterTimestamp(
        accounts: List<AccountCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): List<AccountQueryDto>? {
        saveAccounts(accounts = accounts, timestamp = timestamp, token = token)
        return getAccountsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}