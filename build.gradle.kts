import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val compileKotlin: KotlinCompile by tasks

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.6.10"

    // Apply the application plugin to add support for building a CLI application.
    application

    // Apply the idea plugin
    idea

    // spotless
    id("com.diffplug.spotless") version "6.3.0"

    // versions
    id("com.github.ben-manes.versions") version "0.42.0"
}

object DependencyVersions {
    const val kotlin = "1.6.0"
    const val postgres = "42.3.3"
    const val kotlinLogging = "2.1.21"
    const val kweb = "0.11.2"
    const val kotlinxCoroutines = "1.6.0"
    const val ktor = "1.6.8"
    const val gson = "2.9.0"
    const val guava = "31.1-jre"
    const val systemRules = "1.19.0"
    const val slf4j = "1.7.31"
    const val truth = "1.1.3"
    const val junit = "5.8.2"
    const val junitPlatformConsole = "1.8.2"
    const val mockk = "1.12.3"
    const val testContainers = "1.16.3"
    const val h2db = "2.1.210"
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin 1.5.0 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${DependencyVersions.kotlin}")

    // use ktor
    implementation("io.ktor:ktor-server-core:${DependencyVersions.ktor}")

    // use ktor-netty engine for ktor
    implementation("io.ktor:ktor-server-netty:${DependencyVersions.ktor}")

    // use Kweb
    implementation("com.github.kwebio:kweb-core:${DependencyVersions.kweb}")

    // use KotlinLogging
    implementation("io.github.microutils:kotlin-logging:${DependencyVersions.kotlinLogging}")

    //Coroutines (chapter 8)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.kotlinxCoroutines}")

    // postgres
    implementation("org.postgresql:postgresql:${DependencyVersions.postgres}")

    // coroutine debug

    implementation("org.slf4j:slf4j-api:${DependencyVersions.slf4j}")
    implementation("org.slf4j:slf4j-simple:${DependencyVersions.slf4j}")
    implementation("com.google.code.gson:gson:${DependencyVersions.gson}")
    implementation("com.google.guava:guava:${DependencyVersions.guava}")
    implementation("com.h2database:h2:${DependencyVersions.h2db}")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${DependencyVersions.kotlinxCoroutines}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${DependencyVersions.kotlin}")
    testImplementation("com.github.stefanbirkner:system-rules:${DependencyVersions.systemRules}")
    testImplementation("com.google.truth:truth:${DependencyVersions.truth}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${DependencyVersions.junit}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${DependencyVersions.junit}")
    testImplementation("io.mockk:mockk:${DependencyVersions.mockk}")
    testImplementation("org.testcontainers:testcontainers:${DependencyVersions.testContainers}")
    testImplementation("org.testcontainers:junit-jupiter:${DependencyVersions.testContainers}")
    testImplementation("org.testcontainers:postgresql:${DependencyVersions.testContainers}")

    testRuntimeOnly("org.junit.platform:junit-platform-console:${DependencyVersions.junitPlatformConsole}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${DependencyVersions.junit}")
}

repositories {
    gradlePluginPortal()
    maven("https://jitpack.io")
    mavenCentral()
}

group = "org.aarbizu"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

application {
    mainClass.set("org.aarbizu.baseballDatabankFrontend.MainKt")
}

spotless {
    kotlin {
        ktlint()
    }
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events = setOf(PASSED, FAILED, SKIPPED)
        }
    }

    task("stage") {
        dependsOn("installDist")
    }
}
