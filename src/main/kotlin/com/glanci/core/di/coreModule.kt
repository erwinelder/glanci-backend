package com.glanci.core.di

import com.glanci.core.data.db.GlanciDatabaseProvider
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.data.repository.UpdateTimeRepositoryImpl
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.core.domain.service.UpdateTimeServiceImpl
import org.koin.dsl.module

val coreModule = module {

    /* ------------ Database ------------ */

    single {
        GlanciDatabaseProvider()
    }

    /* ------------ Repositories ------------ */

    single<UpdateTimeRepository> {
        UpdateTimeRepositoryImpl(databaseProvider = get())
    }

    /* ------------ Services ------------ */

    factory<UpdateTimeService> { params ->
        UpdateTimeServiceImpl(
            updateTimeRepository = get(),
            tableName = params.get()
        )
    }

}