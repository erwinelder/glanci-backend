package com.glanci.core.data.db

import com.glanci.account.data.db.AccountTable
import com.glanci.auth.data.db.GlanciUserTable
import com.glanci.auth.domain.model.UserRole
import com.glanci.budget.data.db.BudgetAccountAssociationTable
import com.glanci.budget.data.db.BudgetOnWidgetTable
import com.glanci.budget.data.db.BudgetTable
import com.glanci.category.data.db.CategoryTable
import com.glanci.categoryCollection.data.db.CategoryCollectionCategoryAssociationTable
import com.glanci.categoryCollection.data.db.CategoryCollectionTable
import com.glanci.core.domain.model.app.AppLanguage
import com.glanci.core.domain.model.app.AppSubscription
import com.glanci.core.utils.getCurrentTimestamp
import com.glanci.navigation.data.db.NavigationButtonTable
import com.glanci.personalization.data.db.WidgetTable
import com.glanci.record.data.db.RecordItemTable
import com.glanci.record.data.db.RecordTable
import com.glanci.transfer.data.db.TransferTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun configureUserManagementDatabaseTestData(database: Database) {
    transaction(database) {
        val timestamp = getCurrentTimestamp()

        SchemaUtils.create(
            GlanciUserTable, UpdateTimeTable,
            AccountTable, CategoryTable,
            RecordTable, RecordItemTable, TransferTable,
            CategoryCollectionTable, CategoryCollectionCategoryAssociationTable,
            BudgetTable, BudgetAccountAssociationTable, BudgetOnWidgetTable,
            WidgetTable, NavigationButtonTable
        )

        val recreateDatabaseTestData = System.getenv("RECREATE_DATABASE_TEST_DATA")?.toBoolean()
        if (recreateDatabaseTestData == true) {
            GlanciUserTable.deleteAll()
            UpdateTimeTable.deleteAll()

            AccountTable.deleteAll()
            CategoryTable.deleteAll()

            RecordTable.deleteAll()
            RecordItemTable.deleteAll()
            TransferTable.deleteAll()

            CategoryCollectionTable.deleteAll()
            CategoryCollectionCategoryAssociationTable.deleteAll()

            BudgetTable.deleteAll()
            BudgetAccountAssociationTable.deleteAll()
            BudgetOnWidgetTable.deleteAll()

            WidgetTable.deleteAll()
            NavigationButtonTable.deleteAll()
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
                it[name] = "Expense Category 1"
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
                it[name] = "Expense Category 2"
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
                it[name] = "Expense Subcategory 1.1"
                it[iconName] = "default_icon"
                it[colorName] = "Default"
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            CategoryTable.insert {
                it[userId] = 1
                it[id] = 4
                it[type] = "+"
                it[orderNum] = 1
                it[parentCategoryId] = null
                it[name] = "Income Category 1"
                it[iconName] = "default_icon"
                it[colorName] = "Default"
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
        }

        if (RecordTable.selectAll().empty()) {
            RecordTable.insert {
                it[userId] = 1
                it[id] = 1
                it[date] = timestamp
                it[type] = "-"
                it[accountId] = 1
                it[includeInBudgets] = true
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            RecordItemTable.insert {
                it[userId] = 1
                it[id] = 1
                it[recordId] = 1
                it[totalAmount] = 100.0
                it[quantity] = 1
                it[categoryId] = 2
                it[subcategoryId] = null
                it[note] = "Item 1"
            }
            RecordItemTable.insert {
                it[userId] = 1
                it[id] = 2
                it[recordId] = 1
                it[totalAmount] = 50.0
                it[quantity] = null
                it[categoryId] = 1
                it[subcategoryId] = 3
                it[note] = "Item 2"
            }

            RecordTable.insert {
                it[userId] = 1
                it[id] = 2
                it[date] = timestamp
                it[type] = "+"
                it[accountId] = 2
                it[includeInBudgets] = true
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            RecordItemTable.insert {
                it[userId] = 1
                it[id] = 3
                it[recordId] = 2
                it[totalAmount] = 200.0
                it[quantity] = 2
                it[categoryId] = 4
                it[subcategoryId] = null
                it[note] = "Item 1"
            }
        }

        if (TransferTable.selectAll().empty()) {
            TransferTable.insert {
                it[userId] = 1
                it[id] = 1
                it[date] = timestamp
                it[senderAccountId] = 1
                it[receiverAccountId] = 2
                it[senderAmount] = 100.0
                it[receiverAmount] = 100.0
                it[senderRate] = 1.0
                it[receiverRate] = 1.0
                it[includeInBudgets] = true
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            TransferTable.insert {
                it[userId] = 1
                it[id] = 2
                it[date] = timestamp
                it[senderAccountId] = 2
                it[receiverAccountId] = 1
                it[senderAmount] = 50.0
                it[receiverAmount] = 100.0
                it[senderRate] = 1.0
                it[receiverRate] = 2.0
                it[includeInBudgets] = true
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
        }

        if (CategoryCollectionTable.selectAll().empty()) {
            CategoryCollectionTable.insert {
                it[userId] = 1
                it[id] = 1
                it[orderNum] = 1
                it[name] = "Collection 1"
                it[type] = "-"
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            CategoryCollectionCategoryAssociationTable.insert {
                it[userId] = 1
                it[collectionId] = 1
                it[categoryId] = 1
            }
        }

        if (BudgetTable.selectAll().empty()) {
            BudgetTable.insert {
                it[userId] = 1
                it[id] = 1
                it[amountLimit] = 1000.0
                it[categoryId] = 1
                it[name] = "Budget 1"
                it[repeatingPeriod] = "Monthly"
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            BudgetTable.insert {
                it[userId] = 1
                it[id] = 2
                it[amountLimit] = 500.0
                it[categoryId] = 2
                it[name] = "Budget 2"
                it[repeatingPeriod] = "Weekly"
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            BudgetAccountAssociationTable.insert {
                it[userId] = 1
                it[budgetId] = 1
                it[accountId] = 1
            }
            BudgetAccountAssociationTable.insert {
                it[userId] = 1
                it[budgetId] = 2
                it[accountId] = 2
            }
        }

        if (BudgetOnWidgetTable.selectAll().empty()) {
            BudgetOnWidgetTable.insert {
                it[userId] = 1
                it[budgetId] = 1
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            BudgetOnWidgetTable.insert {
                it[userId] = 1
                it[budgetId] = 2
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
        }

        if (WidgetTable.selectAll().empty()) {
            WidgetTable.insert {
                it[userId] = 1
                it[name] = "Widget 1"
                it[orderNum] = 1
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            WidgetTable.insert {
                it[userId] = 1
                it[name] = "Widget 2"
                it[orderNum] = 2
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            WidgetTable.insert {
                it[userId] = 1
                it[name] = "Widget 3"
                it[orderNum] = 3
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
        }

        if (NavigationButtonTable.selectAll().empty()) {
            NavigationButtonTable.insert {
                it[userId] = 1
                it[screenName] = "Home"
                it[orderNum] = 1
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            NavigationButtonTable.insert {
                it[userId] = 1
                it[screenName] = "Records"
                it[orderNum] = 2
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            NavigationButtonTable.insert {
                it[userId] = 1
                it[screenName] = "CategoryStatistics"
                it[orderNum] = 3
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            NavigationButtonTable.insert {
                it[userId] = 1
                it[screenName] = "Budgets"
                it[orderNum] = 4
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
            NavigationButtonTable.insert {
                it[userId] = 1
                it[screenName] = "Settings"
                it[orderNum] = 5
                it[this.timestamp] = timestamp
                it[deleted] = false
            }
        }

        println("Test data for \"Glanci\" database successfully configured!")
    }
}