package io.kito.kore.util.minecraft

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.datafixers.kinds.App
import com.mojang.serialization.Codec
import com.mojang.serialization.JavaOps
import com.mojang.serialization.JsonOps
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.api.distmarker.Dist
import net.neoforged.neoforge.client.settings.KeyConflictContext
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.FluidType
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import thedarkcolour.kotlinforforge.neoforge.forge.runWhenOn

/**
 * Type alias for [BlockBehaviour.Properties], used for defining block properties.
 */
typealias BlockProp = BlockBehaviour.Properties
/**
 * Type alias for [Item.Properties], used for defining item properties.
 */
typealias  ItemProp = Properties
/**
 * Type alias for [FluidType.Properties], used for defining fluid type properties.
 */
typealias FluidTypeProp = FluidType.Properties
/**
 * Type alias for [BaseFlowingFluid.Properties], used for defining flowing fluid properties.
 */
typealias FlowingFluidProp = BaseFlowingFluid.Properties

/**
 * Constant for the English (US) language code.
 */
const val EN_US = "en_us"
/**
 * Constant for the Brazilian Portuguese language code.
 */
const val PT_BR = "pt_br"

/**
 * Creates a new [Item.Properties] instance.
 * @return A new [Item.Properties] instance.
 */
fun itemProp() = Properties()
/**
 * Creates a new [BlockBehaviour.Properties] instance.
 * @return A new [BlockBehaviour.Properties] instance.
 */
fun blockProp(): BlockBehaviour.Properties = BlockBehaviour.Properties.of()

/**
 * Creates a [Codec] from a [RecordCodecBuilder.Instance].
 * This is a utility function to simplify codec creation.
 * @param T The type of the object the codec will encode/decode.
 * @param builder A lambda that takes a [RecordCodecBuilder.Instance] and defines the codec fields.
 * @return A [Codec] for type [T].
 */
fun <T> recordCodecOf(builder: RecordCodecBuilder.Instance<T>.() -> App<RecordCodecBuilder.Mu<T>, T>): Codec<T> =
    RecordCodecBuilder.create { builder(it) }

/**
 * Creates a [MapCodec] from a [RecordCodecBuilder.Instance].
 * This is a utility function to simplify map codec creation.
 * @param T The type of the object the map codec will encode/decode.
 * @param builder A lambda that takes a [RecordCodecBuilder.Instance] and defines the map codec fields.
 * @return A [MapCodec] for type [T].
 */
fun <T> mapCodecOf(builder: RecordCodecBuilder.Instance<T>.() -> App<RecordCodecBuilder.Mu<T>, T>): MapCodec<T> =
    RecordCodecBuilder.mapCodec { builder(it) }

/**
 * Creates a dynamic [Codec] based on a list of fields and a decoder function.
 * This function supports up to 16 fields for dynamic codec creation.
 * @param T The type of the object the codec will encode/decode.
 * @param fields A list of [App] instances representing the fields of the codec.
 * @param decoder A lambda that takes a list of decoded field values and constructs an object of type [T].
 * @return A dynamic [Codec] for type [T].
 * @throws IllegalArgumentException if the number of fields is not supported (greater than 16).
 */
fun <T> createDynamicCodec(fields: List<App<RecordCodecBuilder.Mu<T>, out Any>>, decoder: (List<Any>) -> T) =
    recordCodecOf {
        when (fields.size) {
             1 ->  group(fields[0]).apply(this) { f0 -> decoder(listOf(f0)); }
             2 ->  group(fields[0], fields[1]).apply(this) { f0, f1 -> decoder(listOf(f0, f1)) }
             3 ->  group(fields[0], fields[1], fields[2]).apply(this) { f0, f1, f2 -> decoder(listOf(f0, f1, f2)) }
             4 ->  group(fields[0], fields[1], fields[2], fields[3]).apply(this) { f0, f1, f2, f3 -> decoder(listOf(f0, f1, f2, f3)) }
             5 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4]).apply(this) { f0, f1, f2, f3, f4 -> decoder(listOf(f0, f1, f2, f3, f4)) }
             6 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]).apply(this) { f0, f1, f2, f3, f4, f5 -> decoder(listOf(f0, f1, f2, f3, f4, f5)) }
             7 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]).apply(this) { f0, f1, f2, f3, f4, f5, f6 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6)) }
             8 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7)) }
             9 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8)) }
            10 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9)) }
            11 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10)) }
            12 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11)) }
            13 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12)) }
            14 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13)) }
            15 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13], fields[14]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14)) }
            16 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13], fields[14], fields[15]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15)) }
            else -> throw IllegalArgumentException("Unsupported number from fields: ${fields.size}")
        }
    }

/**
 * Creates a dynamic [MapCodec] based on a list of fields and a decoder function.
 * This function supports up to 16 fields for dynamic map codec creation.
 * @param T The type of the object the map codec will encode/decode.
 * @param fields A list of [App] instances representing the fields of the map codec.
 * @param decoder A lambda that takes a list of decoded field values and constructs an object of type [T].
 * @return A dynamic [MapCodec] for type [T].
 * @throws IllegalArgumentException if the number of fields is not supported (greater than 16).
 */
fun <T> createDynamicMapCodec(fields: List<App<RecordCodecBuilder.Mu<T>, out Any>>, decoder: (List<Any>) -> T) =
    mapCodecOf {
        when (fields.size) {
            1 ->  group(fields[0]).apply(this) { f0 -> decoder(listOf(f0)); }
            2 ->  group(fields[0], fields[1]).apply(this) { f0, f1 -> decoder(listOf(f0, f1)) }
            3 ->  group(fields[0], fields[1], fields[2]).apply(this) { f0, f1, f2 -> decoder(listOf(f0, f1, f2)) }
            4 ->  group(fields[0], fields[1], fields[2], fields[3]).apply(this) { f0, f1, f2, f3 -> decoder(listOf(f0, f1, f2, f3)) }
            5 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4]).apply(this) { f0, f1, f2, f3, f4 -> decoder(listOf(f0, f1, f2, f3, f4)) }
            6 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]).apply(this) { f0, f1, f2, f3, f4, f5 -> decoder(listOf(f0, f1, f2, f3, f4, f5)) }
            7 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]).apply(this) { f0, f1, f2, f3, f4, f5, f6 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6)) }
            8 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7)) }
            9 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8)) }
            10 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9)) }
            11 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10)) }
            12 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11)) }
            13 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12)) }
            14 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13)) }
            15 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13], fields[14]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14)) }
            16 ->  group(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11], fields[12], fields[13], fields[14], fields[15]).apply(this) { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15 -> decoder(listOf(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15)) }
            else -> throw IllegalArgumentException("Unsupported number from fields: ${fields.size}")
        }
    }

/**
 * Creates a dynamic [StreamCodec] based on a list of fields and a decoder function.
 * This function supports up to 6 fields for dynamic stream codec creation.
 * @param B The type of the buffer used by the stream codec.
 * @param T The type of the object the stream codec will encode/decode.
 * @param fields A list of pairs, where each pair consists of a [StreamCodec] for a field and a getter function for that field from an object of type [T].
 * @param decoder A lambda that takes a list of decoded field values and constructs an object of type [T].
 * @return A dynamic [StreamCodec] for type [T].
 * @throws IllegalArgumentException if the number of fields is not supported (greater than 6).
 */
fun <B, T> createDynamicStreamCodec(
    fields: List<Pair<StreamCodec<B, out Any>, (T) -> Any?>>,
    decoder: (List<Any>) -> T
): StreamCodec<B, T> =
    when (fields.size) {
        1 -> StreamCodec.composite(
            fields[0].first as StreamCodec<B, Any>, fields[0].second
        ) { f0 ->
            decoder(listOf(f0))
        }

        2 -> StreamCodec.composite(
            fields[0].first as StreamCodec<B, Any>, fields[0].second,
            fields[1].first as StreamCodec<B, Any>, fields[1].second
        ) { f0, f1 ->
            decoder(listOf(f0, f1))
        }

        3 -> StreamCodec.composite(
            fields[0].first as StreamCodec<B, Any>, fields[0].second,
            fields[1].first as StreamCodec<B, Any>, fields[1].second,
            fields[2].first as StreamCodec<B, Any>, fields[2].second
        ) { f0, f1, f2 ->
            decoder(listOf(f0, f1, f2))
        }

        4 -> StreamCodec.composite(
            fields[0].first as StreamCodec<B, Any>, fields[0].second,
            fields[1].first as StreamCodec<B, Any>, fields[1].second,
            fields[2].first as StreamCodec<B, Any>, fields[2].second,
            fields[3].first as StreamCodec<B, Any>, fields[3].second
        ) { f0, f1, f2, f3 ->
            decoder(listOf(f0, f1, f2, f3))
        }

        5 -> StreamCodec.composite(
            fields[0].first as StreamCodec<B, Any>, fields[0].second,
            fields[1].first as StreamCodec<B, Any>, fields[1].second,
            fields[2].first as StreamCodec<B, Any>, fields[2].second,
            fields[3].first as StreamCodec<B, Any>, fields[3].second,
            fields[4].first as StreamCodec<B, Any>, fields[4].second
        ) { f0, f1, f2, f3, f4 ->
            decoder(listOf(f0, f1, f2, f3, f4))
        }

        6 -> StreamCodec.composite(
            fields[0].first as StreamCodec<B, Any>, fields[0].second,
            fields[1].first as StreamCodec<B, Any>, fields[1].second,
            fields[2].first as StreamCodec<B, Any>, fields[2].second,
            fields[3].first as StreamCodec<B, Any>, fields[3].second,
            fields[4].first as StreamCodec<B, Any>, fields[4].second,
            fields[5].first as StreamCodec<B, Any>, fields[5].second
        ) { f0, f1, f2, f3, f4, f5 ->
            decoder(listOf(f0, f1, f2, f3, f4, f5))
        }

        else -> throw IllegalArgumentException("Unsupported number of fields: ${fields.size}")
    }



/**
 * Provides an instance of [NbtOps] for working with NBT data.
 */
inline val   nbtOps:  NbtOps get() =  NbtOps.INSTANCE
/**
 * Provides an instance of [JsonOps] for working with JSON data.
 */
inline val  jsonOps: JsonOps get() = JsonOps.INSTANCE
/**
 * Provides a compressed instance of [JsonOps] for working with JSON data.
 */
inline val jsonCOps: JsonOps get() = JsonOps.COMPRESSED
/**
 * Provides an instance of [JavaOps] for working with Java objects.
 */
inline val  javaOps: JavaOps get() = JavaOps.INSTANCE

/**
 * Represents the key system main input type.
 */
inline val keySysMain get() = InputConstants.Type.KEYSYM
/**
 * Represents the mouse mappings input type.
 */
inline val mouseMappings get() = InputConstants.Type.MOUSE

/**
 * Represents the key conflict context for GUI interactions.
 */
inline val guiConflict get() = KeyConflictContext.GUI
/**
 * Represents the key conflict context for in-game interactions.
 */
inline val inGameConflict get() = KeyConflictContext.IN_GAME
/**
 * Represents the key conflict context for universal interactions.
 */
inline val universalConflict get() = KeyConflictContext.UNIVERSAL

/**
 * Extension function to set a [Tag] in a [CompoundTag] using an operator syntax.
 * @param name The name of the tag.
 * @param tag The [Tag] to set.
 */
operator fun CompoundTag.set(name: String, tag: Tag) = put(name, tag)

/**
 * Retrieves the [Minecraft] client instance.
 * Throws an [IllegalStateException] if called on the logical server.
 */
val minecraftClient: Minecraft get() = runForDist(
    { Minecraft.getInstance() },
    { throw IllegalStateException("trying to get the minecraft client on the logical server") })

/**
 * Executes a given lambda with the [Minecraft] client instance, only on the client side.
 * @param run The lambda to execute, taking a [Minecraft] instance as a parameter.
 */
fun withMcClient(run: (Minecraft) -> Unit) {
    runWhenOn(Dist.CLIENT) { run(minecraftClient) }
}

/**
 * Extension property to convert a [String] to a literal [MutableComponent].
 */
inline val String.literal: MutableComponent get() = Component.literal(this)

/**
 * Value class representing a shaped recipe pattern.
 * @property pattern An array of strings defining the shape of the recipe.
 */
@JvmInline
value class Shape(val pattern: Array<out String>) {
    /**
     * Defines the ingredients for the shaped recipe pattern.
     * @param values A vararg of [Pair]s, where each pair consists of a character and its corresponding [Ingredient].
     * @return A [ShapedRecipePattern] instance.
     */
    fun by(vararg values: Pair<Char, Ingredient>) = ShapedRecipePattern.of(mapOf(*values), *pattern)
}

/**
 * Creates a [Shape] for a shaped recipe pattern.
 * @param pattern A vararg of strings defining the shape of the recipe.
 * @return A [Shape] instance.
 */
fun shaped(vararg pattern: String) = Shape(pattern)

/**
 * Extension function to apply a block of code to an [ArgumentBuilder] using an operator syntax.
 * @param T The type of the command source.
 * @param A The type of the [ArgumentBuilder].
 * @param block A lambda that takes the [ArgumentBuilder] and applies configurations.
 * @return The modified [ArgumentBuilder] for fluent chaining.
 */
operator fun <T, A : ArgumentBuilder<T, A>> A.plus(block: A.() -> Unit) = apply(block)

/**
 * Extension function to retrieve a command argument by name and type.
 * @param T The expected type of the argument.
 * @param name The name of the argument.
 * @return The argument value cast to type [T].
 */
inline fun <reified T> CommandContext<*>.arg(name: String): T = getArgument(name, T::class.java)


/**
 * Extension function to define the command execution logic for an [ArgumentBuilder].
 * @param T The type of the command source.
 * @param block A lambda that takes a [CommandContext] and returns an integer (typically 0 for success).
 */
fun <T, A : ArgumentBuilder<T, A>> A.runs(block: CommandContext<T>.() -> Int) { executes(block) }

/**
 * Retrieves the local player instance from the Minecraft client.
 */
inline val localPlayer get() = Minecraft.getInstance().player

/**
 * Extension function to get an [ItemStack] from a [RecipeInput] by index.
 * @param idx The index of the item stack.
 * @return The [ItemStack] at the specified index.
 */
operator fun RecipeInput.get(idx: Int): ItemStack = getItem(idx)

