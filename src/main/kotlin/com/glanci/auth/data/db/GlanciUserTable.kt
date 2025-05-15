package com.glanci.auth.data.db

import org.jetbrains.exposed.sql.Table

object GlanciUserTable : Table("glanci_user") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 50)
    val role = varchar("role", 10)
    val name = varchar("name", 30)
    val langCode = varchar("lang", 2)
    val subscription = varchar("subscription", 15)

    override val primaryKey = PrimaryKey(id)
}