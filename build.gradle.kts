import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

val kotlin = "2.0.0"
val kotlinLogging = "3.0.4"
val kotlinxCoroutines = "1.6.4"
val ktor = "2.3.1"
val guava = "32.0.1-jre"
val systemRules = "1.19.0"
val slf4j = "2.0.7"
val truth = "1.1.5"
val junit = "5.9.2"
val junitPlatformConsole = "1.9.2"
val mockk = "1.13.5"
val testContainers = "1.16.3"
val h2db = "2.1.214"
val serialization = "1.5.1"
val react = "18.2.0-pre.572"
val reactRouterDom = "6.3.0-pre.506"
val emotion = "11.11.1-pre.572"
val kotlinMUIIcons = "5.11.16-pre.572"
val kotlinMUI = "5.13.5-pre.572"
val kotlinBrowser = "1.0.0-pre.572"
val redux = "0.6.1"
val letsplotkotlin = "4.4.1"
val letsplotcommon = "3.2.0"

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("multiplatform") version "2.0.0"

    // Apply the application plugin to add support for building a CLI application.
    application

    kotlin("plugin.serialization") version "2.0.0"

    // Apply the idea plugin
    idea

    // spotless
    id("com.diffplug.spotless") version "6.19.0"

    // versions
    id("com.github.ben-manes.versions") version "0.47.0"
}

kotlin {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
    jvmToolchain(17)
    jvm {
        withJava()

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            testLogging {
                events = setOf(PASSED, FAILED, SKIPPED)
            }
        }
    }

    js(IR) {
        browser {
            binaries.executable()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization")
                implementation("io.ktor:ktor-client-core:$ktor")
                implementation("org.reduxkotlin:redux-kotlin-threadsafe:$redux")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktor")
                implementation("io.ktor:ktor-server-compression:$ktor")
                implementation("io.ktor:ktor-server-content-negotiation:$ktor")
                implementation("io.ktor:ktor-server-cors:$ktor")
                implementation("io.ktor:ktor-server-netty:$ktor")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutines")
                implementation("io.github.microutils:kotlin-logging:$kotlinLogging")
                implementation("org.slf4j:slf4j-api:$slf4j")
                implementation("org.slf4j:slf4j-simple:$slf4j")
                implementation("com.google.guava:guava:$guava")
                implementation("com.h2database:h2:$h2db")
                implementation("org.jetbrains.lets-plot:lets-plot-common:$letsplotcommon")
                implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:$letsplotkotlin")
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

                runtimeOnly("org.junit.platform:junit-platform-console:$junitPlatformConsole")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktor")
                implementation("io.ktor:ktor-client-json:$ktor")
                implementation("io.ktor:ktor-client-content-negotiation:$ktor")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:$emotion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom-legacy:$reactRouterDom")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui:$kotlinMUI")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui-icons:$kotlinMUIIcons")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:$kotlinBrowser")
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
        target("src/**/*.kt", "src/**/*.kts", "src/jvmMain/**/*.kt")
        ktfmt().kotlinlangStyle() // has its own section below
        ktlint() // has its own section below
        // licenseHeader '/* (C)$YEAR */' // or licenseHeaderFile
    }
    kotlinGradle {
        target("*.gradle.kts") // default target for kotlinGradle
        ktlint() // or ktfmt() or prettier()
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

tasks.getByName("distTar") {
    dependsOn("allMetadataJar")
    dependsOn("jsJar")
}

tasks.getByName("distZip") {
    dependsOn("allMetadataJar")
    dependsOn("jsJar")
}

tasks.getByName("jsBrowserProductionWebpack") {
    dependsOn("jsProductionExecutableCompileSync")
    dependsOn("jsDevelopmentExecutableCompileSync")
}

tasks.getByName("jsBrowserDevelopmentWebpack") {
    dependsOn("jsDevelopmentExecutableCompileSync")
    dependsOn("jsProductionExecutableCompileSync")
}

tasks.getByName<Jar>("jvmJar") {
    val taskName = if (project.hasProperty("isProduction") ||
        project.gradle.startParameter.taskNames.contains("installDist")
    ) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    dependsOn(tasks.getByName("jsDevelopmentExecutableCompileSync"))
    from(File(webpackTask.outputDirectory.toString(), webpackTask.mainOutputFileName.toString())) // bring output file along into the JAR
}

tasks.create("stage") {
    dependsOn("installDist")
    doLast {
        delete(fileTree("build").exclude("libs").exclude("install"))
    }
}

tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
