package com.glanci.core.di

import com.glanci.core.data.db.GlanciDatabaseProvider
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.data.repository.UpdateTimeRepositoryImpl
import org.koin.dsl.module

val coreModule = module {

    /* ------------ Database ------------ */

    single {
        GlanciDatabaseProvider()
    }

    /* ------------ Repository ------------ */

    single<UpdateTimeRepository> {
        UpdateTimeRepositoryImpl(databaseProvider = get())
    }

}