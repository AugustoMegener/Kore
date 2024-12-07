package io.kito.kore;

import io.kito.kore.Kore.ID
import io.kito.kore.common.event.registerKoreEvents
import io.kito.kore.common.registry.BlockRegister
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

//import net.neoforged.fml.common.Mod as b

@Mod(ID)
object Kore {
    const val ID = "kore"
    val logger = LogManager.getLogger(ID)!!

    init {
        BlockRegister.register(MOD_BUS)


        ModList.get().mods.forEach {
            if (ID !in it.dependencies.map { d -> d.modId }) return@forEach

            ModList.get().getModContainerById(it.modId).get().eventBus?.let { bus ->
                registerKoreEvents(it.owningFile.file.scanResult, bus)
            }
        }
    }
}