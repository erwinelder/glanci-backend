package com.glanci.account.domain.service

import com.glanci.account.data.repository.AccountRepository
import com.glanci.request.domain.error.AccountError
import com.glanci.account.mapper.toDataModel
import com.glanci.account.mapper.toQueryDto
import com.glanci.account.shared.dto.AccountCommandDto
import com.glanci.account.shared.dto.AccountQueryDto
import com.glanci.account.shared.service.AccountService
import com.glanci.auth.utils.authorizeAtLeastAsUserResult
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.request.domain.*
import com.glanci.request.domain.error.DataError

class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val updateTimeService: UpdateTimeService
) : AccountService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveAccounts(
        accounts: List<AccountCommandDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        runCatching {
            accountRepository.upsertAccounts(
                accounts = accounts.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(AccountError.AccountsNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getAccountsAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<AccountQueryDto>, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }

        val accounts = runCatching {
            accountRepository.getAccountsAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toQueryDto() }
        }.getOrElse {
            return ResultData.Error(AccountError.AccountsNotFetched)
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
            .returnIfError { return ResultData.Error(it) }
        return getAccountsAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}