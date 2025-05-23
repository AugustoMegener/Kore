inline val String.prop get() = project.findProperty(this) as String

val modId = "mod_id".prop

plugins {
    `java-library`
    `maven-publish`
    signing
    idea
    id("net.neoforged.gradle.userdev") version "7.0.170"

    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"

    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

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

runs {
    configureEach {
        systemProperty("forge.logging.markers", "REGISTRIES")
        systemProperty("forge.logging.console.level", "debug")
        modSource(project.sourceSets["main"])
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

    create("data") {
        arguments.addAll(
            "--mod", modId,
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
    implementation(kotlin("reflect"))

    implementation("net.neoforged:neoforge:${"neo_version".prop}")
    implementation("thedarkcolour:kotlinforforge-neoforge:5.5.0")
}

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = listOf("minecraft_version", "minecraft_version_range", "neo_version", "neo_version_range",
                                   "loader_version_range", "mod_id", "mod_name", "mod_license", "mod_version",
                                   "mod_authors", "mod_description").associateWith { it.prop }

    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") { expand(replaceProperties) }
}

println("KEY present? ${System.getenv("OSSRH_USERNAME")}")
println("KEY present? ${System.getenv("OSSRH_PASSWORD")}")

println("KEY present? ${System.getenv("SIGNING_KEY")}")
println("KEY present? ${System.getenv("SINGNING_PASSWORD")}")

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            groupId = "mod_group_id".prop
            artifactId = "mod_id".prop
            version = "mod_version".prop

            artifact(tasks["sourcesJar"])
            from(components["java"])

            pom {
                name.set("mod_name".prop)
                description.set("mod_description".prop)
                url.set("https://github.com/AugustoMegener/Kore")

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer { name.set("Kito") }
                }
                scm {
                    connection.set("scm:git:git://github.com/AugustoMegener/Kore.git")
                    developerConnection.set("scm:git:ssh://github.com:AugustoMegener/Kore.git")
                    url.set("https://github.com/AugustoMegener/Kore")
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        file("private_key.txt").readText(),
        System.getenv("SINGNING_PASSWORD")
    )
    sign(publishing.publications["gpr"])
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