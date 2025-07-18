package com.glanci.core.data.db

import com.glanci.account.data.db.AccountTable
import com.glanci.auth.data.db.GlanciUserTable
import com.glanci.auth.domain.model.UserRole
import com.glanci.category.data.db.CategoryTable
import com.glanci.core.domain.model.app.AppLanguage
import com.glanci.core.domain.model.app.AppSubscription
import com.glanci.core.utils.getCurrentTimestamp
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun configureUserManagementDatabaseTestData(database: Database) {
    transaction(database) {
        val timestamp = getCurrentTimestamp()

        SchemaUtils.create(
            GlanciUserTable, UpdateTimeTable,
            AccountTable, CategoryTable
        )

        val recreateDatabaseTestData = System.getenv("RECREATE_DATABASE_TEST_DATA")?.toBoolean()
        if (recreateDatabaseTestData == true) {
            GlanciUserTable.deleteAll()
            UpdateTimeTable.deleteAll()
            AccountTable.deleteAll()
            CategoryTable.deleteAll()
        }

        if (GlanciUserTable.selectAll().empty()) {
            GlanciUserTable.insert {
                it[id] = 1
                it[email] = "base_user@domain.com"
                it[role] = UserRole.User.name
                it[name] = "Base User"
                it[langCode] = AppLanguage.English.langCode
                it[subscription] = AppSubscription.Base.name
                it[this.timestamp] = timestamp
            }
            GlanciUserTable.insert {
                it[id] = 2
                it[email] = "premium_user@domain.com"
                it[role] = UserRole.User.name
                it[name] = "Premium User"
                it[langCode] = AppLanguage.English.langCode
                it[subscription] = AppSubscription.Premium.name
                it[this.timestamp] = timestamp
            }
            GlanciUserTable.insert {
                it[id] = 3
                it[email] = "admin@domain.com"
                it[role] = UserRole.Admin.name
                it[name] = "Admin"
                it[langCode] = AppLanguage.English.langCode
                it[subscription] = AppSubscription.Premium.name
                it[this.timestamp] = timestamp
            }
        }

        if (UpdateTimeTable.selectAll().empty()) {
            UpdateTimeTable.insert {
                it[userId] = 1
                it[name] = "Account"
                it[this.timestamp] = timestamp
            }
        }

        if (AccountTable.selectAll().empty()) {
            AccountTable.insert {
                it[userId] = 1
                it[id] = 1
                it[orderNum] = 1
                it[name] = "Account 1"
                it[currency] = "USD"
                it[balance] = 1000.0
                it[color] = "Default"
                it[hide] = false
                it[hideBalance] = false
                it[withoutBalance] = false
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            AccountTable.insert {
                it[userId] = 1
                it[id] = 2
                it[orderNum] = 2
                it[name] = "Account 2"
                it[currency] = "EUR"
                it[balance] = 500.0
                it[color] = "Default"
                it[hide] = false
                it[hideBalance] = false
                it[withoutBalance] = false
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
        }

        if (CategoryTable.selectAll().empty()) {
            CategoryTable.insert {
                it[userId] = 1
                it[id] = 1
                it[type] = "-"
                it[orderNum] = 1
                it[parentCategoryId] = null
                it[name] = "Category 1"
                it[iconName] = "default_icon"
                it[colorName] = "Default"
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            CategoryTable.insert {
                it[userId] = 1
                it[id] = 2
                it[type] = "-"
                it[orderNum] = 2
                it[parentCategoryId] = null
                it[name] = "Category 2"
                it[iconName] = "default_icon"
                it[colorName] = "Default"
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            CategoryTable.insert {
                it[userId] = 1
                it[id] = 3
                it[type] = "-"
                it[orderNum] = 3
                it[parentCategoryId] = 1
                it[name] = "Subcategory 1.1"
                it[iconName] = "default_icon"
                it[colorName] = "Default"
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
        }

        println("Test data for \"Glanci\" database successfully configured!")
    }
}