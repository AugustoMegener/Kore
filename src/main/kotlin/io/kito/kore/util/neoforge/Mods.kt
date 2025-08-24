package io.kito.kore.util.neoforge

import io.kito.kore.Kore.ID
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.neoforgespi.language.IModInfo
import net.neoforged.neoforgespi.locating.IModFile

/**
 * Utility object for interacting with NeoForge mod information and the mod list.
 * Provides convenient access to mod containers, mod files, and iteration utilities.
 */
object Mods {
    /**
     * Retrieves the singleton instance of [ModList].
     */
    inline val modList: ModList get() = ModList.get()

    /**
     * Extension property to get the first [IModInfo] from an [IModFile].
     */
    inline val IModFile.info: IModInfo get() = modInfos.first()
    /**
     * Extension property to get the mod ID from an [IModFile].
     */
    inline val IModFile.modId: String  get() = modContainer.modId


    /**
     * Iterates over each [ModContainer] in the [ModList] and applies the given action.
     * @param action A lambda that takes a [ModContainer] and its mod ID.
     */
    fun forEachModContainer(action: ModContainer.(String) -> Unit) { modList.forEachModContainer { id, c -> action(c, id) } }

    /**
     * Iterates over each [IModFile] in the [ModList] and applies the given action.
     * @param action A lambda that takes an [IModFile].
     */
    fun <T> forEachModFile(action: IModFile.() -> T) =
        arrayListOf<T>().apply { modList.forEachModFile { add(it.action()) } }

    /**
     * Iterates over each [IModFile] that either is the Kore mod itself or depends on Kore, and applies the given action.
     * This is useful for finding mods that are directly using the Kore framework.
     * @param action A lambda that takes an [IModFile].
     */
    fun forEachKoreUserFile(action: IModFile.() -> Unit) {
        modList.forEachModFile { file ->
            if (file.info.modId == ID || ID in file.info.dependencies.map { it.modId }) file.apply(action)
        }
    }

    /**
     * Extension property to get the [ModContainer] associated with an [IModFile].
     */
    inline val IModFile.modContainer: ModContainer get() = modList.getModContainerById(info.modId).get()
}

