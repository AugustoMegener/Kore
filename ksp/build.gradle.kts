plugins {
    kotlin("jvm") version "2.1.0"
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"

    id("net.neoforged.gradle.userdev") version "7.0.170"
}

group = "io.kito.kore_ksp"
version = "0.0.1"

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
    mavenCentral()
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("net.neoforged:neoforge:${project.findProperty("neo_version")}")
    implementation("thedarkcolour:kotlinforforge-neoforge:5.5.0")

    implementation(project(":"))

    implementation("com.squareup:kotlinpoet:1.14.2")
    implementation("com.google.dagger:dagger-compiler:2.51.1")
    ksp("com.google.dagger:dagger-compiler:2.51.1")

    implementation("com.google.devtools.ksp:symbol-processing-api:2.1.0-1.0.29")
}

tasks.test {
    useJUnitPlatform()
}