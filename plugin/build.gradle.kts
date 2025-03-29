plugins {
    kotlin("jvm")
    `kotlin-dsl`
    `java-gradle-plugin`

}

group = "io.kito.kore_plugin"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation(localGroovy())

    implementation("org.json:json:20231013")

    //api("net.neoforged.gradle.userdev:7.0.170")
}

gradlePlugin {
    plugins {
        create("kore_plugin") {
            id = "io.kito.kore.plugin"
            implementationClass = "io.kito.kore_plugin.Plugin"
        }
    }
}