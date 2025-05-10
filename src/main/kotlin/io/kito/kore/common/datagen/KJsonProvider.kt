package io.kito.kore.common.datagen

import com.google.gson.JsonElement
import io.kito.kore.common.data.codec.KCodecSerializer
import io.kito.kore.util.minecraft.jsonOps
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.data.PackOutput.Target.DATA_PACK
import java.util.concurrent.CompletableFuture

abstract class KJsonProvider<T : Any>(private val packOutput : PackOutput,
                                      private val target     : PackOutput.Target,
                                      private val modiId      : String,
                                      private val dir        : String,
                                      serializer: () -> KCodecSerializer<T>) : DataProvider
{
    private val serializer by lazy { serializer() }

    private val jsons = hashMapOf<String, JsonElement>()

    abstract fun addData()

    infix fun String.by(data: T) { jsons[this] = serializer.run { data.encode(jsonOps) } }

    override fun run(output: CachedOutput): CompletableFuture<*> {
        addData()

        val path = packOutput.getOutputFolder(DATA_PACK).resolve(modiId).resolve(dir)

        return CompletableFuture.allOf(
            *jsons.map { (n, j) -> DataProvider.saveStable(output, j, path.resolve("${n}.json")) }.toTypedArray()
        )
    }

    override fun getName() = "$modiId's $dir"
}