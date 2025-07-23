package com.glanci.categoryCollection.di

import com.glanci.categoryCollection.data.repository.CategoryCollectionRepository
import com.glanci.categoryCollection.data.repository.CategoryCollectionRepositoryImpl
import com.glanci.categoryCollection.domain.service.CategoryCollectionServiceImpl
import com.glanci.categoryCollection.shared.service.CategoryCollectionService
import com.glanci.core.domain.model.app.TableName
import org.koin.core.parameter.parametersOf
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
            updateTimeService = get {
                parametersOf(TableName.CategoryCollection)
            }
        )
    }

}