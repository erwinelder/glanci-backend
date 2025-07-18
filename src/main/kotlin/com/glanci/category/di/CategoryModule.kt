package com.glanci.category.di

import com.glanci.category.data.repository.CategoryRepository
import com.glanci.category.data.repository.CategoryRepositoryImpl
import com.glanci.category.shared.service.CategoryService
import com.glanci.category.domain.service.CategoryServiceImpl
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
            updateTimeRepository = get()
        )
    }

}