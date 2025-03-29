package io.kito.kore.common.resource

import com.google.common.collect.ImmutableMap
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import io.kito.kore.common.data.codec.KCodecSerializer
import io.kito.kore.util.minecraft.jsonOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller

abstract class KJsonResourceReloadListener<T : Any>(val dir: String, val serializerProvider: () -> KCodecSerializer<T>) :
    SimpleJsonResourceReloadListener(GsonBuilder().setPrettyPrinting().create(), dir)
{
    private val serializer by lazy { serializerProvider() }

    lateinit var entries: ImmutableMap<ResourceLocation, T> private set
    val values get() = entries.values

    override fun apply(obj: MutableMap<ResourceLocation, JsonElement>,
                       resourceManager: ResourceManager,
                       profiler: ProfilerFiller)
    {
        entries = ImmutableMap.copyOf(obj.mapValues { (_, json) -> serializer.decode(jsonOps, json) })
    }
}