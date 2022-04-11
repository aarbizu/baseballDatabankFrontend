import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

 val kotlin = "1.6.20"
 val postgres = "42.3.3"
 val kotlinLogging = "2.1.21"
 val kweb = "0.11.2"
 val kotlinxCoroutines = "1.6.0"
 val ktor = "1.6.8"
 val gson = "2.9.0"
 val guava = "31.1-jre"
 val systemRules = "1.19.0"
 val slf4j = "1.7.31"
 val truth = "1.1.3"
 val junit = "5.8.2"
 val junitPlatformConsole = "1.8.2"
 val mockk = "1.12.3"
 val testContainers = "1.16.3"
 val h2db = "2.1.210"
 val serialization = "1.3.2"
 val react = "17.0.2-pre.299-kotlin-1.6.10"

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("multiplatform") version "1.6.20"

    // Apply the application plugin to add support for building a CLI application.
    application

    kotlin("plugin.serialization") version "1.6.20"

    // Apply the idea plugin
    idea

    // spotless
    id("com.diffplug.spotless") version "6.4.1"

    // versions
    id("com.github.ben-manes.versions") version "0.42.0"
}

kotlin {
    jvm {
        withJava()

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            testLogging {
                events = setOf(PASSED, FAILED, SKIPPED)
            }
        }
    }
    js {
        browser {
            binaries.executable()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization")
                implementation("io.ktor:ktor-client-core:$ktor")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:$kotlinxCoroutines")
                implementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin")
                implementation("com.github.stefanbirkner:system-rules:$systemRules")
                implementation("com.google.truth:truth:$truth")
                implementation("org.junit.jupiter:junit-jupiter-api:$junit")
                implementation("org.junit.jupiter:junit-jupiter-params:$junit")
                implementation("io.mockk:mockk:$mockk")
                implementation("org.testcontainers:testcontainers:$testContainers")
                implementation("org.testcontainers:junit-jupiter:$testContainers")
                implementation("org.testcontainers:postgresql:$testContainers")

                runtimeOnly("org.junit.platform:junit-platform-console:$junitPlatformConsole")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-serialization:$ktor")
                implementation("io.ktor:ktor-server-core:$ktor")
                implementation("io.ktor:ktor-server-netty:$ktor")
                implementation("com.github.kwebio:kweb-core:$kweb")
                implementation("io.github.microutils:kotlin-logging:$kotlinLogging")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutines")
                implementation("org.postgresql:postgresql:$postgres")
                implementation("org.slf4j:slf4j-api:$slf4j")
                implementation("org.slf4j:slf4j-simple:$slf4j")
                implementation("com.google.code.gson:gson:$gson")
                implementation("com.google.guava:guava:$guava")
                implementation("com.h2database:h2:$h2db")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktor")
                implementation("io.ktor:ktor-client-json:$ktor")
                implementation("io.ktor:ktor-client-serialization:$ktor")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$react")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    gradlePluginPortal()
}

group = "org.aarbizu"
version = "0.1.0"

application {
    mainClass.set("org.aarbizu.baseballDatabankFrontend.MainKt")
}

spotless {
    kotlin {
        ktlint()
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

distributions {
    main {
        contents {
            from("$buildDir/libs") {
                rename("${rootProject.name}-jvm", rootProject.name)
                into("lib")
            }
        }
    }
}

tasks.getByName<Jar>("jvmJar") {
    val taskName = if (project.hasProperty("isProduction")
        || project.gradle.startParameter.taskNames.contains("installDist")
    ) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) // bring output file along into the JAR
}

tasks.create("stage") {
    dependsOn("installDist")
}

tasks.getByName<JavaExec>("run") {
        classpath(tasks.getByName<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}