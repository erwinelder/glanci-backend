package com.glanci.navigation.di

import com.glanci.navigation.data.repository.NavigationButtonRepository
import com.glanci.navigation.data.repository.NavigationButtonRepositoryImpl
import com.glanci.navigation.domain.service.NavigationButtonServiceImpl
import com.glanci.navigation.shared.service.NavigationButtonService
import org.koin.dsl.module

val navigationButtonModule = module {

    /* ---------- Repositories ---------- */

    single< NavigationButtonRepository> {
        NavigationButtonRepositoryImpl(databaseProvider = get())
    }

    /* ---------- Services ---------- */

    single<NavigationButtonService> {
        NavigationButtonServiceImpl(
            navigationButtonRepository = get(),
            updateTimeRepository = get()
        )
    }

}