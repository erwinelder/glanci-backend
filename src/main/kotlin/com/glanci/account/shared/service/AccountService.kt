package com.glanci.account.shared.service

import com.glanci.account.shared.dto.AccountCommandDto
import com.glanci.account.shared.dto.AccountQueryDto
import com.glanci.request.domain.ResultData
import com.glanci.request.domain.error.DataError
import com.glanci.request.domain.SimpleResult
import kotlinx.rpc.annotations.Rpc

@Rpc
interface AccountService {

    suspend fun getUpdateTime(token: String): ResultData<Long, DataError>

    suspend fun saveAccounts(accounts: List<AccountCommandDto>, timestamp: Long, token: String): SimpleResult<DataError>

    suspend fun getAccountsAfterTimestamp(timestamp: Long, token: String): ResultData<List<AccountQueryDto>, DataError>

    suspend fun saveAccountsAndGetAfterTimestamp(
        accounts: List<AccountCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<AccountQueryDto>, DataError>

}