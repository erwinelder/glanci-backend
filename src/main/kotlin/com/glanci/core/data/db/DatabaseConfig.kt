package com.glanci.core.data.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.apply

fun configureDatabase(
    environment: String,
    databaseName: String,
    poolSize: Int = 2
): Database {
    val dbUrl = System.getenv("DATABASE_URL") ?: error("Missing DATABASE_URL environment variable")
    val dbUsername = System.getenv("DATABASE_USERNAME") ?: error("Missing DATABASE_USERNAME environment variable")
    val dbPassword = System.getenv("DATABASE_PASSWORD") ?: error("Missing DATABASE_PASSWORD environment variable")

    val config = HikariConfig().apply {
        jdbcUrl = "$dbUrl/$databaseName"
        username = dbUsername
        password = dbPassword
        driverClassName = "org.postgresql.Driver"
        maximumPoolSize = poolSize
    }

    val database = Database.connect(datasource = HikariDataSource(config))

    transaction(database) {
        println("Connected to Glanci \"$databaseName\" database in \"$environment\" environment successfully!")
    }

    return database
}