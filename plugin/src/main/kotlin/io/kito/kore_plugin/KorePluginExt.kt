package io.kito.kore_plugin


import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class KorePluginExt @Inject constructor(objects: ObjectFactory) {
    lateinit var moddevGraldePluginVersion: String

    internal val parchment: Parchment = objects.newInstance(Parchment::class.java)

    open class Parchment {
        var autoConfigueParchment = true

        var parchmentMcVersion: String? = null
        var parchmentChannel = "releases"
        var parchmentVersion: String? = null
    }

    internal val neoforge: Neoforge = objects.newInstance(Neoforge::class.java)

    open class Neoforge {
        lateinit var version: String //21.1.83
        lateinit var versionRange: String
    }

    internal val info: Info = objects.newInstance(Info::class.java, this)

    open class Info @Inject constructor(parent: KorePluginExt) {
        var name = parent.modId
        var license = "MIT"
        var version = "0.0.1-pre"
        var authors = ""
        var description = ""
    }

    lateinit var modId: String
    lateinit var modGroup: String
    lateinit var version: String
    lateinit var versionRange: String
    lateinit var modVersion: String
    lateinit var kffVersion: String
    lateinit var loaderVersionRange: String

    fun parchment(block: Parchment.() -> Unit) { parchment.apply(block) }
    fun neoforge (block: Neoforge .() -> Unit) { neoforge .apply(block) }
    fun info     (block: Info     .() -> Unit) { info     .apply(block) }
}