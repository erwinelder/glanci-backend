package com.glanci.record.di

import com.glanci.core.domain.model.app.TableName
import com.glanci.record.data.repository.RecordRepository
import com.glanci.record.data.repository.RecordRepositoryImpl
import com.glanci.record.domain.service.RecordServiceImpl
import com.glanci.record.shared.service.RecordService
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val recordModule = module {

    /* ---------- Repositories ---------- */

    single<RecordRepository> {
        RecordRepositoryImpl(databaseProvider = get())
    }

    /* ---------- Services ---------- */

    single<RecordService> {
        RecordServiceImpl(
            recordRepository = get(),
            updateTimeService = get {
                parametersOf(TableName.Record)
            }
        )
    }

}