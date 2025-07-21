package com.glanci.core.config

import com.glanci.account.di.accountModule
import com.glanci.auth.di.authModule
import com.glanci.budget.di.budgetModule
import com.glanci.category.di.categoryModule
import com.glanci.categoryCollection.di.categoryCollectionModule
import com.glanci.core.di.coreModule
import com.glanci.navigation.di.navigationButtonModule
import com.glanci.personalization.di.personalizationModule
import com.glanci.record.di.recordModule
import com.glanci.transfer.di.transferModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(
            coreModule, authModule,
            accountModule, categoryModule,
            recordModule, transferModule,
            categoryCollectionModule, budgetModule,
            personalizationModule, navigationButtonModule
        )
    }
}
