package com.glanci.account.shared.service

import com.glanci.account.domain.dto.AccountCommandDto
import com.glanci.account.domain.dto.AccountQueryDto
import kotlinx.rpc.annotations.Rpc

@Rpc
interface AccountService {

    suspend fun getUpdateTime(token: String): Long?

    suspend fun saveAccounts(accounts: List<AccountCommandDto>, timestamp: Long, token: String)

    suspend fun getAccountsAfterTimestamp(timestamp: Long, token: String): List<AccountQueryDto>?

    suspend fun saveAccountsAndGetAfterTimestamp(
        accounts: List<AccountCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): List<AccountQueryDto>?

}