package io.kito.kore

import io.kito.kore.util.minecraft.ResourceLocationExt.loc
import net.minecraft.client.KeyMapping

/**
 * An abstract utility class providing common functionalities for mods within the Kore framework.
 * It simplifies the creation of `ResourceLocation` objects and localized keys by encapsulating the mod ID.
 *
 * @property modId The unique identifier of the mod using this utility.
 */
abstract class ModUtil(private val modId: String) {

    /**
     * Creates a `ResourceLocation` for a given path, prefixed with this mod's ID.
     * This is useful for referencing assets, registries, and other mod-specific resources.
     *
     * @param path The path to the resource, relative to the mod's domain.
     * @return A `ResourceLocation` object representing the local resource.
     */
    fun local(path: String) = loc(modId, path)

    /**
     * Generates a localized key for a specific item or block, prefixed with "key." and the mod ID.
     * This is commonly used for keybinding localization.
     *
     * @param name The name of the key (e.g., "my_keybinding").
     * @return A string representing the localized key.
     */
    fun keyLocale(name: String) = "key.$modId.$name"

    /**
     * Generates a localized key for a keybinding category, prefixed with "key.category.".
     * This helps in organizing keybindings within the game's control settings.
     *
     * @param name The name of the keybinding category (e.g., "my_mod_category").
     * @return A string representing the localized key category.
     */
    fun keyCategoryLocale(name: String) = KeyMapping.Category(local(name))
}

