plugins {
    `java-library`
    `maven-publish`
    idea
    id("net.neoforged.gradle.userdev") version "7.0.170"

    kotlin("jvm") version "2.1.0"
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.BIN
}

version = project.findProperty("mod_version") as String
group = project.findProperty("mod_group_id") as String

repositories {
    mavenLocal()
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

base {
    archivesName = project.findProperty("mod_id") as String
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

runs {
    configureEach {
        systemProperty("forge.logging.markers", "REGISTRIES")
        systemProperty("forge.logging.console.level", "debug")
        modSource(project.sourceSets["main"])
    }

    create("client") {
        systemProperty("forge.enabledGameTestNamespaces", project.findProperty("mod_id") as String)
    }

    create("server") {
        systemProperty("forge.enabledGameTestNamespaces", project.findProperty("mod_id") as String)
        argument("--nogui")
    }

    create("gameTestServer") {
        systemProperty("forge.enabledGameTestNamespaces", project.findProperty("mod_id") as String)
    }

    create("data") {
        arguments.addAll(
                "--mod", project.findProperty("mod_id") as String,
                "--all", "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
        )
    }
}

sourceSets["main"].resources.srcDir("src/generated/resources")

configurations {
    runtimeClasspath.configure { extendsFrom(configurations.localRuntime.get()) }
}

dependencies {
    implementation("net.neoforged:neoforge:${project.findProperty("neo_version")}")
    implementation("thedarkcolour:kotlinforforge-neoforge:5.5.0")

    implementation("com.squareup:kotlinpoet:1.14.2")
    implementation("com.google.dagger:dagger-compiler:2.51.1")
    ksp("com.google.dagger:dagger-compiler:2.51.1")

    implementation("com.google.devtools.ksp:symbol-processing-api:2.1.0-1.0.29")
}

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
            "minecraft_version" to project.findProperty("minecraft_version"),
            "minecraft_version_range" to project.findProperty("minecraft_version_range"),
            "neo_version" to project.findProperty("neo_version"),
            "neo_version_range" to project.findProperty("neo_version_range"),
            "loader_version_range" to project.findProperty("loader_version_range"),
            "mod_id" to project.findProperty("mod_id"),
            "mod_name" to project.findProperty("mod_name"),
            "mod_license" to project.findProperty("mod_license"),
            "mod_version" to project.findProperty("mod_version"),
            "mod_authors" to project.findProperty("mod_authors"),
            "mod_description" to project.findProperty("mod_description")
    )
    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
    repositories {
        maven { url = uri("file://${project.projectDir}/repo") }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true

        /*sourceDirs = sourceDirs + file("build/generated/ksp/main/kotlin") // or tasks["kspKotlin"].destination
        generatedSourceDirs = generatedSourceDirs + file("build/generated/ksp/main/kotlin") + file("build/generated/ksp/test/kotlin")*/
    }
}
