package com.glanci.auth.data.repository

import com.glanci.auth.data.db.GlanciUserTable
import com.glanci.auth.domain.model.User
import com.glanci.core.data.db.GlanciDatabaseProvider
import com.glanci.core.domain.model.app.AppLanguage
import com.glanci.auth.shared.dto.AppSubscription
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl(
    private val database: Database
) : UserRepository {

    constructor(databaseProvider: GlanciDatabaseProvider) : this(database = databaseProvider.database)


    override fun getUser(id: Int): User? {
        return transaction(database) {
            GlanciUserTable
                .selectAll()
                .where { GlanciUserTable.id eq id }
                .map {
                    User(
                        id = it[GlanciUserTable.id],
                        email = it[GlanciUserTable.email],
                        role = enumValueOf(it[GlanciUserTable.role]),
                        name = it[GlanciUserTable.name],
                        language = AppLanguage.fromLangCode(langCode = it[GlanciUserTable.langCode]) ?: return@map null,
                        subscription = enumValueOf(it[GlanciUserTable.subscription]),
                        timestamp = it[GlanciUserTable.timestamp]
                    )
                }
                .singleOrNull()
        }
    }

    override fun getUser(email: String): User? {
        return transaction(database) {
            GlanciUserTable
                .selectAll()
                .where { GlanciUserTable.email eq email }
                .map {
                    User(
                        id = it[GlanciUserTable.id],
                        email = it[GlanciUserTable.email],
                        role = enumValueOf(it[GlanciUserTable.role]),
                        name = it[GlanciUserTable.name],
                        language = AppLanguage.fromLangCode(langCode = it[GlanciUserTable.langCode]) ?: return@map null,
                        subscription = enumValueOf(it[GlanciUserTable.subscription]),
                        timestamp = it[GlanciUserTable.timestamp]
                    )
                }
                .singleOrNull()
        }
    }

    override fun searchUsers(query: String): List<User> {
        val likeQuery = "%$query%"

        return transaction(database) {
            GlanciUserTable
                .selectAll()
                .where {
                    (GlanciUserTable.email.lowerCase() like likeQuery.lowercase()) or
                            (GlanciUserTable.name.lowerCase() like likeQuery.lowercase())
                }
                .mapNotNull {
                    User(
                        id = it[GlanciUserTable.id],
                        email = it[GlanciUserTable.email],
                        role = enumValueOf(it[GlanciUserTable.role]),
                        name = it[GlanciUserTable.name],
                        language = AppLanguage.fromLangCode(langCode = it[GlanciUserTable.langCode])
                            ?: return@mapNotNull null,
                        subscription = enumValueOf(it[GlanciUserTable.subscription]),
                        timestamp = it[GlanciUserTable.timestamp]
                    )
                }
        }
    }

    override fun getAllUsers(): List<User> {
        return transaction(database) {
            GlanciUserTable.selectAll().mapNotNull {
                User(
                    id = it[GlanciUserTable.id],
                    email = it[GlanciUserTable.email],
                    role = enumValueOf(it[GlanciUserTable.role]),
                    name = it[GlanciUserTable.name],
                    language = AppLanguage.fromLangCode(langCode = it[GlanciUserTable.langCode]) ?: return@mapNotNull null,
                    subscription = enumValueOf(it[GlanciUserTable.subscription]),
                    timestamp = it[GlanciUserTable.timestamp]
                )
            }
        }
    }

    override fun createUser(user: User): Int {
        return transaction(database) {
            GlanciUserTable.insert {
                it[email] = user.email
                it[role] = user.role.name
                it[name] = user.name
                it[langCode] = user.language.langCode
                it[subscription] = user.subscription.name
                it[timestamp] = user.timestamp
            }
        } get GlanciUserTable.id
    }

    override fun saveUserName(userId: Int, name: String) {
        transaction(database) {
            GlanciUserTable.update({ GlanciUserTable.id eq userId }) {
                it[GlanciUserTable.name] = name
            }
        }
    }

    override fun saveUserLanguage(userId: Int, language: AppLanguage, timestamp: Long) {
        transaction(database) {
            GlanciUserTable.update({ GlanciUserTable.id eq userId }) {
                it[GlanciUserTable.langCode] = language.langCode
                it[GlanciUserTable.timestamp] = timestamp
            }
        }
    }

    override fun saveUserEmail(userId: Int, email: String) {
        transaction(database) {
            GlanciUserTable.update({ GlanciUserTable.id eq userId }) {
                it[GlanciUserTable.email] = email
            }
        }
    }

    override fun saveUserSubscription(userId: Int, subscription: AppSubscription) {
        transaction(database) {
            GlanciUserTable.update({ GlanciUserTable.id eq userId }) {
                it[GlanciUserTable.subscription] = subscription.name
            }
        }
    }

    override fun deleteUser(userId: Int) {
        transaction(database) {
            GlanciUserTable.deleteWhere { id eq userId }
        }
    }

}