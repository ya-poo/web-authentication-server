plugins {
    kotlin("jvm") version "1.9.20"
    id("io.ktor.plugin") version "2.2.4"
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
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-jackson")
    implementation("io.ktor:ktor-server-status-pages-jvm")

    implementation("io.insert-koin:koin-ktor:3.5.3")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.3")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("com.webauthn4j:webauthn4j-core:0.20.7.RELEASE")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("com.upokecenter:cbor:4.5.3")
}
