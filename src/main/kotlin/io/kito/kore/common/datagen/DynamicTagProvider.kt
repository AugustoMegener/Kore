package io.kito.kore.common.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

class DynamicTagProvider(registry: Registry<Any>,
                         registryKey: ResourceKey<out Registry<Any>>,
                         val resources: List<() -> Pair<Any, TagKey<Any>>>,
                         val optionalResources: List<() -> Pair<Any, TagKey<Any>>>,
                         val tags: List<() -> Pair<TagKey<Any>, TagKey<Any>>>,
                         val optionalTags: List<() -> Pair<TagKey<Any>, TagKey<Any>>>,
                         output: PackOutput,
                         lookupProvider: CompletableFuture<HolderLookup.Provider>,
                         modId: String, ) :
    IntrinsicHolderTagsProvider<Any>(
        output,
        registryKey,
        lookupProvider,
        { it: Any -> registry.getResourceKey(it).getOrNull() },
        modId
    )
{
    override fun addTags(provider: HolderLookup.Provider) {
        resources.map { it() }.forEach { (key, tag) -> this.tag(tag).add(key) }
        optionalResources.map { it() }.forEach { (loc, tag) -> tag(tag).addOptional(loc) }
        tags.map { it() }.forEach { (tag1, tag2) -> tag(tag2).addTag(tag1) }
        optionalTags.map { it() }.forEach { (loc, tag) -> tag(tag).addOptionalTag(loc) }
    }
}