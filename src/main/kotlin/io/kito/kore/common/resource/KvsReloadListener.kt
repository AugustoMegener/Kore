package io.kito.kore.common.resource

import io.kito.kore.Kore.ID
import io.kito.kore.client.gui.kanvas.KvsScript
import io.kito.kore.client.gui.kanvas.errorScreen
import io.kito.kore.client.gui.kanvas.obj.KanvasBuilder
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.resources.ResourceLocation
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.api.ResultWithDiagnostics

@RegisterReloadListener("$ID:kanvas")
object KvsReloadListener : ScriptValueReloadListener<KanvasBuilder<Any>>(KotlinType(KvsScript::class)) {

    private lateinit var kanvas: Map<ResourceLocation, KanvasBuilder<Any>>

    override fun applyResult(obj: Map<ResourceLocation, KanvasBuilder<Any>>) { kanvas = obj }

    override fun onFail(loc: ResourceLocation, res: ResultWithDiagnostics.Failure) =
        errorScreen(loc, res)

    @Suppress(UNCHECKED_CAST)
    fun getKanvas(loc: ResourceLocation) = kanvas[loc] as? KanvasBuilder<Unit>?

    @Suppress(UNCHECKED_CAST)
    fun <T> getContextKanvas(loc: ResourceLocation) = kanvas[loc] as? KanvasBuilder<T>?
}