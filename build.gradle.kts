import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.4.0"

    // Apply the application plugin to add support for building a CLI application.
    application

    // Apply the idea plugin
    idea

    // spotless
    id("com.diffplug.spotless") version "5.1.1"

    // versions
    id("com.github.ben-manes.versions") version "0.29.0"
}

repositories {
    maven("https://jitpack.io")
    mavenCentral()
    // Use jcenter for resolving dependencies.
    jcenter()
}

object DependencyVersions {
    const val postgres = "42.2.15"
    const val kotlinLogging= "1.8.3"
    const val kweb = "0.7.20"
    const val kotlinxCoroutines = "1.3.8"
    const val kotlinxCoroutinesDebug = "1.3.8"
    const val ktor = "1.3.2"
    //const val okhttp = "4.8.0"
    const val gson = "2.8.6"
    const val guava = "29.0-jre"
    const val systemRules = "1.19.0"
    const val slf4j = "2.0.0-alpha1"
    const val truth = "1.0.1"
    const val junit = "5.7.0-RC1"
    const val junitPlatformConsole = "1.7.0-RC1"
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // use ktor
    implementation("io.ktor:ktor-server-core:${DependencyVersions.ktor}")

    // use ktor-netty engine for ktor
    implementation("io.ktor:ktor-server-netty:${DependencyVersions.ktor}")

    // use Kweb
    implementation("com.github.kwebio:kweb-core:${DependencyVersions.kweb}")

    // use KotlinLogging
    implementation("io.github.microutils:kotlin-logging:${DependencyVersions.kotlinLogging}")

    //Coroutines (chapter 8)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${DependencyVersions.kotlinxCoroutines}")

    // postgres
    implementation("org.postgresql:postgresql:${DependencyVersions.postgres}")

    // coroutine debug

    implementation("org.slf4j:slf4j-api:${DependencyVersions.slf4j}")
    implementation("org.slf4j:slf4j-simple:${DependencyVersions.slf4j}")
    implementation("com.google.code.gson:gson:${DependencyVersions.gson}")
    implementation("com.google.guava:guava:${DependencyVersions.guava}")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${DependencyVersions.kotlinxCoroutinesDebug}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("com.github.stefanbirkner:system-rules:${DependencyVersions.systemRules}")
    testImplementation("com.google.truth:truth:${DependencyVersions.truth}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${DependencyVersions.junit}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${DependencyVersions.junit}")

    testRuntimeOnly("org.junit.platform:junit-platform-console:${DependencyVersions.junitPlatformConsole}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${DependencyVersions.junit}")
}

group = "org.aarbizu"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}

application {
    // Define the main class for the application.
    mainClassName = "org.aarbizu.baseballDatabankFrontend.MainKt"
}

spotless {
    kotlin {
        ktlint()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(PASSED, FAILED, SKIPPED)
    }
}
