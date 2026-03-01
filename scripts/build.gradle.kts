plugins {
    id("java")
    kotlin("jvm") version "2.2.0"
}

group = "io.kito"
version = "1.21.10-0.1.0b"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    (implementation(kotlin("compiler-embeddable"))!!)
    (implementation(kotlin("daemon-embeddable"))!!)
    (implementation(kotlin("scripting-common"))!!)
    (implementation(kotlin("scripting-compiler-embeddable"))!!)
    (implementation(kotlin("scripting-compiler-impl-embeddable"))!!)
    (implementation(kotlin("scripting-jvm"))!!)
    (implementation(kotlin("scripting-jvm-host"))!!)
    (implementation(kotlin("script-runtime"))!!)
}

tasks.test {
    useJUnitPlatform()
}