package io.kito.kore.common.datagen

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import io.kito.kore.util.minecraft.jsonOps
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.data.PackOutput.Target.DATA_PACK
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

abstract class KJsonProvider<T : Any>(private val packOutput : PackOutput,
                                      private val target     : PackOutput.Target,
                                      private val modiId      : String,
                                      private val dir        : String,
                                      serializer: () -> Codec<T>) : DataProvider
{
    private val serializer by lazy { serializer() }

    private val jsons = hashMapOf<ResourceLocation, JsonElement>()

    abstract fun addData()

    infix fun ResourceLocation.by(data: T) { jsons[this] = serializer.encodeStart(jsonOps, data).getOrThrow() }

    override fun run(output: CachedOutput): CompletableFuture<*> {
        addData()

        val path = packOutput.getOutputFolder(DATA_PACK)

        return CompletableFuture.allOf(
            *jsons.map { (n, j) ->
                DataProvider.saveStable(output, j, path.resolve(n.namespace).resolve(dir).resolve("${n.path}.json"))
            }.toTypedArray()
        )
    }

    override fun getName() = "$modiId's $dir"
}

