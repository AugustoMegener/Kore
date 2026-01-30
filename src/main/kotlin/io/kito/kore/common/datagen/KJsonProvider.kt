package io.kito.kore.common.datagen

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import io.kito.kore.common.data.codec.KCodecSerializer
import io.kito.kore.util.minecraft.jsonOps
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.data.PackOutput.Target.DATA_PACK
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

/**
 * Abstract base class for data providers that generate JSON files based on a [KCodecSerializer].
 * This class simplifies the process of creating data generators for custom data structures
 * that can be serialized to JSON using Kore's codec system.
 *
 * @param T The type of the data object that will be serialized to JSON.
 * @param packOutput The [PackOutput] for writing generated data.
 * @param target The [PackOutput.Target] indicating where the JSON files should be saved (e.g., [DATA_PACK]).
 * @param modiId The mod ID for which data is being generated.
 * @param dir The subdirectory within the mod's data folder where the JSON files will be saved.
 * @param serializer A lambda that provides a [Codec] instance for the data type [T].
 */
abstract class KJsonProvider<T : Any>(private val packOutput : PackOutput,
                                      private val target     : PackOutput.Target,
                                      private val modiId      : String,
                                      private val dir        : String,
                                      serializer: () -> Codec<T>) : DataProvider
{
    /**
     * Lazily initialized [KCodecSerializer] instance for the data type [T].
     */
    private val serializer by lazy { serializer() }

    /**
     * A map to store the generated JSON elements, keyed by their file name.
     */
    private val jsons = hashMapOf<ResourceLocation, JsonElement>()

    /**
     * Abstract method that subclasses must implement to add their data to the [jsons] map.
     * This is where the data objects are created and associated with their file names.
     */
    abstract fun addData()

    /**
     * Infix function to associate a data object with a file name.
     * The data object will be serialized to JSON using the [serializer] and stored in the [jsons] map.
     *
     * @receiver The file name (String) for the JSON output.
     * @param data The data object of type [T] to be serialized.
     */
    infix fun ResourceLocation.by(data: T) { jsons[this] = serializer.encodeStart(jsonOps, data).getOrThrow() }

    /**
     * Runs the data generation process.
     * This method calls [addData] to populate the [jsons] map, that saves each JSON element to a file.
     *
     * @param output The [CachedOutput] for efficient writing of data.
     * @return A [CompletableFuture] that completes when all JSON files have been saved.
     */
    override fun run(output: CachedOutput): CompletableFuture<*> {
        addData()

        val path = packOutput.getOutputFolder(DATA_PACK)

        return CompletableFuture.allOf(
            *jsons.map { (n, j) ->
                DataProvider.saveStable(output, j, path.resolve(n.namespace).resolve(dir).resolve("${n.path}.json"))
            }.toTypedArray()
        )
    }

    /**
     * Returns the name of this data provider, used for logging and identification.
     * @return A string representing the name of the data provider.
     */
    override fun getName() = "$modiId's $dir"
}

