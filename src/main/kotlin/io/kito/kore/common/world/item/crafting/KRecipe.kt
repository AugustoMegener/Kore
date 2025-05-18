package io.kito.kore.common.world.item.crafting

import com.mojang.datafixers.util.Either
import io.kito.kore.util.UNCHECKED_CAST
import io.kito.kore.util.minecraft.get
import net.minecraft.core.HolderLookup
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.crafting.ICustomIngredient
import net.neoforged.neoforge.common.crafting.SizedIngredient
import kotlin.reflect.KProperty1
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

@Suppress(UNCHECKED_CAST)
abstract class KRecipe<T : RecipeInput>(private val type: RecipeType<*>? = null,
                                        val recipeSerializer: RecipeSerializer<*>? = null) : Recipe<T>
{
    private val slots by lazy {
        this::class.memberProperties
            .filter { it.hasAnnotation<Slot>() }
            .map {
                it.findAnnotation<Slot>()!! to run {
                    when (val value = (it as KProperty1<Any, Any>).call(this)) {
                        is ICustomIngredient -> Either.right(value)
                        is Ingredient -> Either.left(SizedIngredient(value, 1 ))
                        is SizedIngredient -> Either.left(value)
                        else -> throw IllegalStateException("Slot annotated field must return either Ingredient or SizedIngredient")
                    }
                }
            }
    }

    private val result by lazy {
        val fld = this::class.memberProperties.find { it.hasAnnotation<Result>() } ?: throw IllegalStateException("No Result annotated fields")
        val fld2 = (fld as KProperty1<Any, Any>)
        val fld3 = fld2.call(this) as? ItemStack

        fld3 ?: throw IllegalStateException("Result annotated field don't return a ItemStack")
    }

    val itemResult: Item by lazy { result.item }

    override fun matches(input: T, level: Level): Boolean {
        slots.forEach { (s, i) -> if (!checkInput(s.slot, i, input, level)) return false }
        return true
    }

    override fun assemble(input: T, registries: HolderLookup.Provider): ItemStack {
        slots.forEach { (s, i) -> assembleInput(s.slot, i, input, registries) }
        return getResultItem(registries)
    }

    override fun canCraftInDimensions(width: Int, height: Int) = true

    override fun getResultItem(registries: HolderLookup.Provider) = result.copy()

    override fun getSerializer() = recipeSerializer ?: this::class.companionObject!!.objectInstance as RecipeSerializer<*>

    override fun getType() = type ?: (this::class.companionObject!!.objectInstance as KRecipeType<*>).type

    fun checkInput(slot: Int, ingredient: Either<SizedIngredient, ICustomIngredient>, input: T, level: Level): Boolean =
        ingredient.map({ checkInput(slot, it, input, level) }, { checkInput(slot, it, input, level)  })

    open fun checkInput(slot: Int, ingredient: SizedIngredient, input: T, level: Level): Boolean =
        ingredient.test(input[slot])

    open fun checkInput(slot: Int, ingredient: ICustomIngredient, input: T, level: Level): Boolean =
        ingredient.test(input[slot])

    fun assembleInput(slot: Int,
                           ingredient: Either<SizedIngredient, ICustomIngredient>,
                           input: T,
                           registries: HolderLookup.Provider)
    {
        ingredient.map(
            { assembleInput(slot, it, input, registries) },
            { assembleInput(slot, it, input, registries) }
        )
    }

    open fun assembleInput(slot: Int,
                           ingredient: SizedIngredient,
                           input: T,
                           registries: HolderLookup.Provider) {}

    open fun assembleInput(slot: Int,
                           ingredient: ICustomIngredient,
                           input: T,
                           registries: HolderLookup.Provider) {}
}