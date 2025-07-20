package com.glanci.categoryCollection.di

import com.glanci.categoryCollection.data.repository.CategoryCollectionRepository
import com.glanci.categoryCollection.data.repository.CategoryCollectionRepositoryImpl
import com.glanci.categoryCollection.shared.service.CategoryCollectionService
import com.glanci.categoryCollection.domain.service.CategoryCollectionServiceImpl
import org.koin.dsl.module

val categoryCollectionModule = module {

    /* ---------- Repositories ---------- */

    single<CategoryCollectionRepository> {
        CategoryCollectionRepositoryImpl(databaseProvider = get())
    }

    /* ---------- Services ---------- */

    single<CategoryCollectionService> {
        CategoryCollectionServiceImpl(
            categoryCollectionRepository = get(),
            updateTimeRepository = get()
        )
    }

}