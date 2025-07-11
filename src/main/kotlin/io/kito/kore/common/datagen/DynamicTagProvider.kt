package io.kito.kore.common.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class DynamicTagProvider(registryKey: ResourceKey<out Registry<Any>>,
                         val resources: List<() -> Pair<ResourceKey<Any>, TagKey<Any>>>,
                         val optionalResources: List<() -> Pair<ResourceLocation, TagKey<Any>>>,
                         val tags: List<() -> Pair<TagKey<Any>, TagKey<Any>>>,
                         val optionalTags: List<() -> Pair<ResourceLocation, TagKey<Any>>>,
                         output: PackOutput,
                         lookupProvider: CompletableFuture<HolderLookup.Provider>,
                         modId: String,
                         fileHelper: ExistingFileHelper) :
    TagsProvider<Any>(output, registryKey, lookupProvider, modId, fileHelper)
{
    override fun addTags(provider: HolderLookup.Provider) {
        resources.map { it() }.forEach { (key, tag) -> tag(tag).add(key) }
        optionalResources.map { it() }.forEach { (loc, tag) -> tag(tag).addOptional(loc) }
        tags.map { it() }.forEach { (tag1, tag2) -> tag(tag2).addTag(tag1) }
        optionalTags.map { it() }.forEach { (loc, tag) -> tag(tag).addOptionalTag(loc) }
    }
}