package io.kito.kore.common.resource

import io.kito.kore.Kore.ID
import io.kito.kore.client.gui.kanvas.KvsBuilder
import io.kito.kore.client.gui.kanvas.errorScreen
import io.kito.kore.client.renderer.RegisterClientReloadListener
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore_scripts.KvsScript
import net.minecraft.resources.ResourceLocation
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.api.ResultWithDiagnostics

@RegisterClientReloadListener("$ID:kanvas")
object KvsReloadListener : ScriptValueReloadListener<KvsBuilder<Any>>(KotlinType(KvsScript::class)) {

    private lateinit var kanvas: Map<ResourceLocation, KvsBuilder<Any>>

    override fun applyResult(obj: Map<ResourceLocation, KvsBuilder<Any>>) { kanvas = obj }

    override fun onFail(loc: ResourceLocation, res: ResultWithDiagnostics.Failure) =
        errorScreen(loc, res)

    @Suppress(UNCHECKED_CAST)
    fun getKanvas(loc: ResourceLocation) = kanvas[loc] as? KvsBuilder<Unit>?

    @Suppress(UNCHECKED_CAST)
    fun <T> getContextKanvas(loc: ResourceLocation) = kanvas[loc] as? KvsBuilder<T>?
}