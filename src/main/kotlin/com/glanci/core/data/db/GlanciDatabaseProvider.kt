package com.glanci.core.data.db

import org.jetbrains.exposed.sql.Database

class GlanciDatabaseProvider {

    val database: Database

    init {
        val environment = System.getenv("ENVIRONMENT")
        database = configureDatabase(environment = environment, databaseName = "glanci_db", poolSize = 1)

        if (environment == "development") {
            configureUserManagementDatabaseTestData(database = database)
        }
    }

}