package com.glanci.budget.di

import com.glanci.budget.data.repository.BudgetRepository
import com.glanci.budget.data.repository.BudgetRepositoryImpl
import com.glanci.budget.domain.service.BudgetServiceImpl
import com.glanci.budget.shared.service.BudgetService
import org.koin.dsl.module

val budgetModule = module {

    /* ---------- Repositories ---------- */

    single<BudgetRepository> {
        BudgetRepositoryImpl(databaseProvider = get())
    }

    /* ---------- Services ---------- */

    single<BudgetService> {
        BudgetServiceImpl(
            budgetRepository = get(),
            updateTimeRepository = get()
        )
    }

}