package io.kito.kore_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.kotlin.dsl.getByName as byName
//import net.neoforged.gradle.dsl.common.runs.run.Run

class KorePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val ext = target.extensions.create("mod", KorePluginExt::class.java, target)

        target.version = ext.info.version
        target.group = ext.modGroup

        target.pluginManager.apply("net.neoforged.gradle.userdev:${ext.moddevGraldePluginVersion}")
        target.pluginManager.apply("com.google.devtools.ksp")

        if (ext.parchment.autoConfigueParchment) {
            val mcVersion = ext.parchment.parchmentMcVersion ?: ext.modVersion

            target.extra.set("neogradle.subsystems.parchment.minecraftVersion", mcVersion)

            target.extra.set(
                "neogradle.subsystems.parchment.mappingsVersion",
                ext.parchment.parchmentVersion ?:
                fetchJson("https://versioning.parchmentmc.org/versions")
                    .getJSONObject(ext.parchment.parchmentChannel)
                    .getString(mcVersion)
            )
        }

        target.extensions.byName<SourceSetContainer>("sourceSets").run {
            getByName("main") { resources.srcDir("src/generated/resources") }
        }

        target.afterEvaluate {
            target.repositories.maven {
                name = "Kotlin for Forge"
                setUrl("https://thedarkcolour.github.io/KotlinForForge/")
            }

            target.tasks.withType(AbstractArchiveTask::class.java) {
                archiveBaseName.set(ext.modId)
            }

            target.dependencies.apply {
                add("implementation", "net.neoforged:neoforge:${ext.neoforge.version}")
                add("implementation", "thedarkcolour:kotlinforforge-neoforge:${ext.kffVersion}")

                //implementation(project(":"))
                //ksp(project(":ksp"))
            }

            target.tasks.withType<ProcessResources>().configureEach {
                val replaceProperties = mapOf(
                    "minecraft_version" to ext.version,
                    "minecraft_version_range" to ext.versionRange,
                    "neo_version" to ext.neoforge.version,
                    "neo_version_range" to ext.neoforge.versionRange,
                    "loader_version_range" to ext.loaderVersionRange,
                    "mod_id" to ext.modId,
                    "mod_name" to ext.info.name,
                    "mod_license" to ext.info.license,
                    "mod_version" to ext.info.version,
                    "mod_authors" to ext.info.authors,
                    "mod_description" to ext.info.description
                )

                inputs.properties(replaceProperties)

                filesMatching("META-INF/neoforge.mods.toml") {
                    expand(replaceProperties)
                }
            }

            //target.tasks.withType<Run>()
        }
    }
}