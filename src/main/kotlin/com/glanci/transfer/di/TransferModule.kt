package com.glanci.transfer.di

import com.glanci.core.domain.model.app.TableName
import com.glanci.transfer.data.repository.TransferRepository
import com.glanci.transfer.data.repository.TransferRepositoryImpl
import com.glanci.transfer.domain.service.TransferServiceImpl
import com.glanci.transfer.shared.service.TransferService
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val transferModule = module {

    /* ---------- Repositories ---------- */

    single<TransferRepository> {
        TransferRepositoryImpl(databaseProvider = get())
    }

    /* ---------- Services ---------- */

    single<TransferService> {
        TransferServiceImpl(
            transferRepository = get(),
            updateTimeService = get {
                parametersOf(TableName.Transfer)
            }
        )
    }

}