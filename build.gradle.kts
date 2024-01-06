plugins {
    kotlin("jvm") version "1.9.20"
    id("io.ktor.plugin") version "2.2.1"
    kotlin("plugin.serialization") version "1.9.20"
}

group = "me.yapoo"
version = "0.0.1"
application {
    mainClass.set("me.yapoo.fido2.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.2.2")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.2.2")
    implementation("io.ktor:ktor-server-netty-jvm:2.2.2")
    implementation("io.ktor:ktor-server-content-negotiation:2.2.2")
    implementation("io.ktor:ktor-serialization-jackson:2.2.2")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.2.2")
    implementation("io.insert-koin:koin-ktor:3.3.0")
    implementation("io.insert-koin:koin-logger-slf4j:3.3.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")
    implementation("com.webauthn4j:webauthn4j-core:0.20.7.RELEASE")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("io.ktor:ktor-server-cors-jvm:2.2.2")
    implementation("com.upokecenter:cbor:4.5.3")
}
