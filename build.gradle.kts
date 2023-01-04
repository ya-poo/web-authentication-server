plugins {
    kotlin("jvm") version "1.8.0"
    id("io.ktor.plugin") version "2.2.1"
}

group = "me.yapoo"
version = "0.0.1"
application {
    mainClass.set("me.yapoo.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.2.1")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.2.1")
    implementation("io.ktor:ktor-server-netty-jvm:2.2.1")
    implementation("io.insert-koin:koin-ktor:3.3.0")
    implementation("io.insert-koin:koin-logger-slf4j:3.3.0")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.2.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.0")
}
