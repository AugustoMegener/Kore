package io.kito.kore;

import io.kito.kore.Kore.ID
import io.kito.kore.common.registry.BlockRegister
import net.neoforged.fml.common.Mod
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

//import net.neoforged.fml.common.Mod as b

@Mod(ID)
object Kore {
    const val ID = "kore"

    init {
        BlockRegister.register(MOD_BUS)
    }
}
