package com.glanci.budget.di

import com.glanci.budget.data.repository.BudgetOnWidgetRepository
import com.glanci.budget.data.repository.BudgetOnWidgetRepositoryImpl
import com.glanci.budget.data.repository.BudgetRepository
import com.glanci.budget.data.repository.BudgetRepositoryImpl
import com.glanci.budget.domain.service.BudgetOnWidgetServiceImpl
import com.glanci.budget.domain.service.BudgetServiceImpl
import com.glanci.budget.shared.service.BudgetOnWidgetService
import com.glanci.budget.shared.service.BudgetService
import com.glanci.core.domain.model.app.TableName
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val budgetModule = module {

    /* ---------- Repositories ---------- */

    single<BudgetRepository> {
        BudgetRepositoryImpl(databaseProvider = get())
    }

    single<BudgetOnWidgetRepository> {
        BudgetOnWidgetRepositoryImpl(databaseProvider = get())
    }

    /* ---------- Services ---------- */

    single<BudgetService> {
        BudgetServiceImpl(
            budgetRepository = get(),
            updateTimeService = get {
                parametersOf(TableName.Budget)
            }
        )
    }

    single<BudgetOnWidgetService> {
        BudgetOnWidgetServiceImpl(
            budgetOnWidgetRepository = get(),
            updateTimeService = get {
                parametersOf(TableName.BudgetOnWidget)
            }
        )
    }

}