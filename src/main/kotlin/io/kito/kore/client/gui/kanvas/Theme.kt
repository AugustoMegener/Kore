package io.kito.kore.client.gui.kanvas

import io.kito.kore.client.gui.kanvas.obj.KvsNode
import io.kito.kore.client.gui.kanvas.obj.Sprite.Companion.sprite
import io.kito.kore.client.gui.kanvas.obj.Texture.Companion.texture
import io.kito.kore.client.gui.kanvas.transform.KvsTransform
import io.kito.kore.client.gui.kanvas.transform.KvsVec
import io.kito.kore.common.config.KoreConfigClient.kanvasTheme
import io.kito.kore.util.minecraft.ResourceLocationExt.gui
import io.kito.kore.util.minecraft.ResourceLocationExt.on
import io.kito.kore.util.minecraft.ResourceLocationExt.plus
import io.kito.kore.util.minecraft.ResourceLocationExt.png
import io.kito.kore.util.minecraft.ResourceLocationExt.texture

object Theme {

    val guiBgSpriteLocation get() = themeLoc("gui_background")
    val guiBg2SpriteLocation get() = themeLoc("gui_background_2")
    val textAreaSpriteLocation get() = themeLoc("text_area")
    val textFrameSpriteLocation get() = themeLoc("text_frame")

    val koreLogoTextureLocation get() = themeLoc("kore_logo").gui.texture.png

    fun themeLoc(path: String) = "themes/" on kanvasTheme + "/$path"

    fun KvsNode.guiBackground(transform: KvsTransform) =
        sprite(guiBgSpriteLocation, transform)

    fun KvsNode.guiBackground(pos: KvsVec, scale: KvsVec) =
        sprite(guiBgSpriteLocation, pos, scale)

    fun KvsNode.secondaryGuiBackground(transform: KvsTransform) =
        sprite(guiBg2SpriteLocation, transform)

    fun KvsNode.secondaryGuiBackground(pos: KvsVec, scale: KvsVec) =
        sprite(guiBg2SpriteLocation, pos, scale)

    fun KvsNode.textArea(transform: KvsTransform) =
        sprite(textAreaSpriteLocation, transform)

    fun KvsNode.textArea(pos: KvsVec, scale: KvsVec) =
        sprite(textAreaSpriteLocation, pos, scale)

    fun KvsNode.textFrame(transform: KvsTransform) =
        sprite(textFrameSpriteLocation, transform)

    fun KvsNode.textFrame(pos: KvsVec, scale: KvsVec) =
        sprite(textFrameSpriteLocation, pos, scale)

    fun KvsNode.koreLogo(transform: KvsTransform) =
        texture(koreLogoTextureLocation, transform)

    fun KvsNode.koreLogo(pos: KvsVec, scale: KvsVec) =
        texture(koreLogoTextureLocation, pos, scale)
}