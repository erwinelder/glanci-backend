package com.glanci.personalization.di

import com.glanci.personalization.data.repository.WidgetRepository
import com.glanci.personalization.data.repository.WidgetRepositoryImpl
import com.glanci.personalization.domain.service.WidgetServiceImpl
import com.glanci.personalization.shared.service.WidgetService
import org.koin.dsl.module

val personalizationModule = module {

    /* ---------- Repositories ---------- */

    single<WidgetRepository> {
        WidgetRepositoryImpl(databaseProvider = get())
    }

    /* ---------- Services ---------- */

    single<WidgetService> {
        WidgetServiceImpl(
            widgetRepository = get(),
            updateTimeRepository = get()
        )
    }

}