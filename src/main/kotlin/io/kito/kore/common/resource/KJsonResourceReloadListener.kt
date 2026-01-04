package io.kito.kore.common.resource

import com.google.common.collect.ImmutableMap
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import io.kito.kore.common.data.codec.KCodecSerializer
import io.kito.kore.util.minecraft.jsonOps
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller

abstract class KJsonResourceReloadListener<T : Any>(dir: FileToIdConverter, serializerProvider: () -> Codec<T>) :
    SimpleJsonResourceReloadListener<T>(serializerProvider(), dir)
{

    lateinit var entries: ImmutableMap<ResourceLocation, T> private set
    val values get() = entries.values

    override fun apply(`object`: Map<ResourceLocation, T>, resourceManager: ResourceManager, profiler: ProfilerFiller) {
        entries = ImmutableMap.copyOf(`object`)
    }
}