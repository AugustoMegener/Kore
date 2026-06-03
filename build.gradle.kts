inline val String.prop get() = project.findProperty(this) as String

val modId = "mod_id".prop

plugins {
    `java-library`
    `maven-publish`
    signing
    idea
    id("net.neoforged.gradle.userdev") version "7.0.+"

    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"

    id("com.google.devtools.ksp") version "2.2.21-2.0.4"
}

jarJar.enable()

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.BIN
}

version = "mod_version".prop
group = "mod_group_id".prop

repositories {
    mavenLocal()
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

base {
    archivesName = modId
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

val libraries by configurations.creating


configurations {

    implementation.get().also { it.isCanBeResolved = true }.extendsFrom(libraries)
}

runs {
    configureEach {
        systemProperty("forge.logging.markers", "REGISTRIES")
        systemProperty("forge.logging.console.level", "debug")
        modSource(project.sourceSets["main"])

        dependencies {
            runtime(configurations.implementation.get().extendsFrom(libraries))
        }
    }

    create("client") {
        systemProperty("forge.enabledGameTestNamespaces", modId)
    }

    create("server") {
        systemProperty("forge.enabledGameTestNamespaces", modId)
        argument("--nogui")
    }

    create("gameTestServer") {
        systemProperty("forge.enabledGameTestNamespaces", modId)
    }

    create("clientData") {
        arguments.addAll(
            "--mod", project.findProperty("mod_id") as String,
            "--all", "--output", file("src/generated/resources/").absolutePath,
            "--existing", file("src/main/resources/").absolutePath
        )
    }

    create("serverData") {
        arguments.addAll(
            "--mod", project.findProperty("mod_id") as String,
            "--all", "--output", file("src/generated/resources/").absolutePath,
            "--existing", file("src/main/resources/").absolutePath
        )
    }
}

sourceSets["main"].resources.srcDir("src/generated/resources")

dependencies {
    implementation(kotlin("reflect"))

    jarJar(implementation(project(":scripts"))!!)

    implementation("net.neoforged:neoforge:${"neo_version".prop}")
    implementation("thedarkcolour:kotlinforforge-neoforge:6.0.0")

    (implementation(kotlin("compiler-embeddable"))!!)
    (implementation(kotlin("daemon-embeddable"))!!)
    (implementation(kotlin("scripting-common"))!!)
    (implementation(kotlin("scripting-compiler-embeddable"))!!)
    (implementation(kotlin("scripting-compiler-impl-embeddable"))!!)
    (implementation(kotlin("scripting-jvm"))!!)
    (implementation(kotlin("scripting-jvm-host"))!!)
    (implementation(kotlin("script-runtime"))!!)
}

ksp {
    arg("projectDir", project.projectDir.path)
}

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = listOf("minecraft_version", "minecraft_version_range", "neo_version", "neo_version_range",
                                   "loader_version_range", "mod_id", "mod_name", "mod_license", "mod_version",
                                   "mod_authors", "mod_description").associateWith { it.prop }

    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") { expand(replaceProperties) }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}



publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }

    repositories {
        maven { url = uri("file://${project.projectDir}/site") }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
