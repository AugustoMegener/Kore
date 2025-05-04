package io.kito.kore_tests.common.template

import io.kito.kore.common.event.RegisterKit
import io.kito.kore.common.reflect.Scan
import io.kito.kore.common.template.Template.Companion.on
import io.kito.kore.common.template.TemplateKit.Companion.kitOf
import io.kito.kore_tests.common.registry.Blocks.blockTemplate
import io.kito.kore_tests.common.registry.FluidTypes.fluidTemplate
import io.kito.kore_tests.common.registry.Items.itemTemplate
import io.kito.kore_tests.common.template.Recipes.recipeTemplate

@Scan
object Kits {

    @RegisterKit
    val niceKit = kitOf(
        blockTemplate,
        itemTemplate,
        recipeTemplate,
        fluidTemplate
    ).on(
        "nice",
        "fool",
        "cute",
        "weird"
    )
}