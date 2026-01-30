package io.kito.kore.common.config

import io.kito.kore.Kore.ID
import io.kito.kore.Kore.local
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.minecraft.ResourceLocationExt.toLoc
import net.neoforged.fml.config.ModConfig

@Scan
object KoreConfigClient : KConfig(ModConfig.Type.CLIENT) {

    val kanvasThemeId: String? by Value { define("kanvas_theme", "$ID:default") }

    val kanvasTheme get() = kanvasThemeId?.toLoc() ?: local("default")
}