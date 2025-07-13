package com.glanci.account.data.repository

import com.glanci.account.data.model.AccountDataModel

interface AccountRepository {

    fun upsertAccounts(accounts: List<AccountDataModel>)

    fun getAccountsAfterTimestamp(userId: Int, timestamp: Long): List<AccountDataModel>

}