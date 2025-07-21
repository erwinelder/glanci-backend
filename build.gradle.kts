group = "com.glanci"
version = "5.0"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.krpc)
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "com.glanci.ApplicationKt"
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Client
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.websockets)
    implementation(libs.ktor.client.logging)
    // Ktor Server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.http.redirect)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.websockets)
    // kRPC
    implementation(libs.kotlinx.rpc.core)
    implementation(libs.krpc.client)
    implementation(libs.krpc.ktor.client)
    implementation(libs.krpc.server)
    implementation(libs.krpc.ktor.server)
    implementation(libs.krpc.serialization.protobuf)
    // Security
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.client.content.negotiation)
    // Database
    implementation(libs.hikaricp)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.postgresql)
    implementation(libs.h2)
    // Firebase
    implementation(libs.firebase.admin)
    // Koin
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    // Utilities
    implementation(libs.kotlinx.datetime)
    implementation(libs.logback.classic)
    // Testing
    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.koin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.krpc.test)
    testImplementation(libs.kotlin.test.junit5)
    configurations.all {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-junit")
    }
}

tasks.test {
    environment("ENVIRONMENT", "development")
    environment("JWT_SECRET", "development-secret")
    environment("JWT_ISSUER", "walletglance-backend.net")
    environment("JWT_AUDIENCE", "glanci-mobile")
    environment("JWT_REALM", "glanci-backend")
    environment("DATABASE_URL", "jdbc:postgresql://localhost:5432")
    environment("DATABASE_USERNAME", "postgres")
    environment("DATABASE_PASSWORD", "postgres")
    environment("RECREATE_DATABASE_TEST_DATA", "true")
    useJUnitPlatform()
}