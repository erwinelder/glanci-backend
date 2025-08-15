package com.glanci.account.domain.service

import com.glanci.account.data.repository.AccountRepository
import com.glanci.account.mapper.toDataModel
import com.glanci.account.mapper.toQueryDto
import com.glanci.account.shared.dto.AccountCommandDto
import com.glanci.account.shared.dto.AccountQueryDto
import com.glanci.account.shared.service.AccountService
import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.request.shared.ResultData
import com.glanci.request.shared.SimpleResult
import com.glanci.request.shared.getOrElse
import com.glanci.request.shared.onError
import com.glanci.request.shared.error.AccountDataError
import com.glanci.request.shared.error.DataError

class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val updateTimeService: UpdateTimeService
) : AccountService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, DataError> {
        val user = authorizeAtLeastAsUser(token = token).getOrElse { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveAccounts(
        accounts: List<AccountCommandDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<DataError> {
        val user = authorizeAtLeastAsUser(token = token).getOrElse { return SimpleResult.Error(it) }

        runCatching {
            accountRepository.upsertAccounts(
                accounts = accounts.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(AccountDataError.AccountsNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getAccountsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<AccountQueryDto>, DataError> {
        val user = authorizeAtLeastAsUser(token = token).getOrElse { return ResultData.Error(it) }

        val accounts = runCatching {
            accountRepository.getAccountsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toQueryDto() }
        }.getOrElse {
            return ResultData.Error(AccountDataError.AccountsNotFetched)
        }

        return ResultData.Success(data = accounts)
    }

    override suspend fun saveAccountsAndGetAfterTimestamp(
        accounts: List<AccountCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<AccountQueryDto>, DataError> {
        saveAccounts(accounts = accounts, timestamp = timestamp, token = token)
            .onError { return ResultData.Error(it) }
        return getAccountsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}