package com.glanci.core.data.db

import com.glanci.auth.data.db.GlanciUserTable
import com.glanci.auth.domain.model.UserRole
import com.glanci.core.domain.model.app.AppLanguage
import com.glanci.core.domain.model.app.AppSubscription
import com.glanci.core.utils.getCurrentTimestamp
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun configureUserManagementDatabaseTestData(database: Database) {
    transaction(database) {
        SchemaUtils.create(
            GlanciUserTable,
        )

        val recreateDatabaseTestData = System.getenv("RECREATE_DATABASE_TEST_DATA")?.toBoolean()
        if (recreateDatabaseTestData == true) {
            GlanciUserTable.deleteAll()
        }

        if (GlanciUserTable.selectAll().empty()) {
            GlanciUserTable.insert {
                it[id] = 1
                it[email] = "free_user@gmail.com"
                it[role] = UserRole.User.name
                it[name] = "Free User"
                it[langCode] = AppLanguage.English.langCode
                it[subscription] = AppSubscription.Free.name
                it[timestamp] = getCurrentTimestamp()
            }
            GlanciUserTable.insert {
                it[id] = 2
                it[email] = "premium_user@gmail.com"
                it[role] = UserRole.User.name
                it[name] = "Premium User"
                it[langCode] = AppLanguage.English.langCode
                it[subscription] = AppSubscription.Premium.name
                it[timestamp] = getCurrentTimestamp()
            }
            GlanciUserTable.insert {
                it[id] = 3
                it[email] = "admin@gmail.com"
                it[role] = UserRole.Admin.name
                it[name] = "Admin"
                it[langCode] = AppLanguage.English.langCode
                it[subscription] = AppSubscription.Premium.name
                it[timestamp] = getCurrentTimestamp()
            }
        }

        println("Test data for \"Glanci\" database successfully configured!")
    }
}