import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.72"

    // Apply the application plugin to add support for building a CLI application.
    application

    // Apply the idea plugin
    idea

    // spotless
    id("com.diffplug.gradle.spotless") version "4.0.1"

    id("com.github.ben-manes.versions") version "0.28.0"
}

repositories {
    // Use jcenter for resolving dependencies.
    jcenter()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")
    implementation("com.squareup.okhttp3:okhttp:4.8.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.google.guava:guava:29.0-jre")
    testImplementation("com.github.stefanbirkner:system-rules:1.19.0")
    testImplementation("com.google.truth:truth:1.0.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0-M1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0-M1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0-M1")
    testRuntimeOnly("org.junit.platform:junit-platform-console:1.7.0-M1")
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
}

group = "org.aarbizu"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

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
