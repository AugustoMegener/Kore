package io.kito.kore.util

import io.kito.kore.Kore.ID
import net.neoforged.fml.ModList
import net.neoforged.neoforgespi.locating.IModFile



val modList get() = ModList.get()

val IModFile.info get() = modInfos.first()

fun forEachKoreUserFile(action: IModFile.() -> Unit) {
    modList.forEachModFile { file ->
        if (file.info.modId == ID || ID in file.info.dependencies.map { it.modId }) file.apply(action)
    }
}

val IModFile.modContainer get() = modList.getModContainerById(info.modId).get()