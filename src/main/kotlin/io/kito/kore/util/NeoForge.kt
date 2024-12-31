package io.kito.kore.util

import io.kito.kore.Kore.ID
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.neoforgespi.language.IModInfo
import net.neoforged.neoforgespi.locating.IModFile


val modList: ModList get() = ModList.get()

val IModFile.info: IModInfo get() = modInfos.first()
val IModFile.modId: String  get() = modContainer.modId

fun forEachModContainer(action: ModContainer.(String) -> Unit) { modList.forEachModContainer { id, c -> action(c, id) } }

fun forEachModFile(action: IModFile.() -> Unit) { modList.forEachModFile { it.apply(action) } }

fun forEachKoreUserFile(action: IModFile.() -> Unit) {
    modList.forEachModFile { file ->
        if (file.info.modId == ID || ID in file.info.dependencies.map { it.modId }) file.apply(action)
    }
}

val IModFile.modContainer: ModContainer get() = modList.getModContainerById(info.modId).get()