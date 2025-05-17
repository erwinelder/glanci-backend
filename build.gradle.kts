group = "com.glanci"
version = "4.2.0"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
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
    // Database
    implementation(libs.hikaricp)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.postgresql)
    implementation(libs.h2)
    // Security
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    // Serialization
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.client.content.negotiation)
    // Ktor Server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.http.redirect)
    implementation(libs.ktor.server.status.pages)
    // Ktor Client
    implementation(libs.ktor.client.cio.jvm)
    // Koin
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    // Firebase
    implementation(libs.firebase.admin)
    // Utilities
    implementation(libs.kotlinx.datetime)
    implementation(libs.logback.classic)
    // Testing
    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.koin.test)
    testImplementation(libs.mockk)
}

tasks.test {
    environment("ENVIRONMENT", "development")
    environment("JWT_SECRET", "test-secret")
    environment("JWT_ISSUER", "glanci-backend.net")
    environment("JWT_AUDIENCE", "glanci-mobile")
    environment("JWT_REALM", "glanci-backend")
    useJUnitPlatform()
}