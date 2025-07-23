package com.glanci.category.di

import com.glanci.category.data.repository.CategoryRepository
import com.glanci.category.data.repository.CategoryRepositoryImpl
import com.glanci.category.domain.service.CategoryServiceImpl
import com.glanci.category.shared.service.CategoryService
import com.glanci.core.domain.model.app.TableName
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val categoryModule = module {

    /* ---------- Repositories ---------- */

    single<CategoryRepository> {
        CategoryRepositoryImpl(databaseProvider = get())
    }

    /* ---------- Services ---------- */

    single<CategoryService> {
        CategoryServiceImpl(
            categoryRepository = get(),
            updateTimeService = get {
                parametersOf(TableName.Category)
            }
        )
    }

}