package com.glanci.core.di

import com.glanci.core.data.db.GlanciDatabaseProvider
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.data.repository.UpdateTimeRepositoryImpl
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val coreModule = module {

    /* ---------- Other ---------- */

    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    /* ------------ Database ------------ */

    single {
        GlanciDatabaseProvider()
    }

    /* ------------ Repository ------------ */

    single<UpdateTimeRepository> {
        UpdateTimeRepositoryImpl(databaseProvider = get())
    }

}