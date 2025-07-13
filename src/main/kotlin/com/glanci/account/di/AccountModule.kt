package com.glanci.account.di

import com.glanci.account.data.repository.AccountRepository
import com.glanci.account.data.repository.AccountRepositoryImpl
import com.glanci.account.domain.service.AccountServiceImpl
import com.glanci.account.shared.service.AccountService
import org.koin.dsl.module

val accountModule = module {

    /* ------------ Repositories ------------ */

    single<AccountRepository> {
        AccountRepositoryImpl(databaseProvider = get())
    }

    /* ------------ Services ------------ */

    single<AccountService> {
        AccountServiceImpl(
            accountRepository = get(),
            updateTimeRepository = get()
        )
    }

}