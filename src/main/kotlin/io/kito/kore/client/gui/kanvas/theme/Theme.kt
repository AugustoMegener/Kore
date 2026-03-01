package io.kito.kore.client.gui.kanvas.theme

import io.kito.kore.client.gui.kanvas.node.KvsNode
import io.kito.kore.client.gui.kanvas.node.Sprite.Companion.sprite
import io.kito.kore.client.gui.kanvas.node.Texture.Companion.texture
import io.kito.kore.client.gui.kanvas.theme.KvsSprite.Companion.kvsSprite
import io.kito.kore.client.gui.kanvas.theme.KvsTexture.Companion.kvsTexture
import io.kito.kore.client.gui.kanvas.transform.KvsTransform
import io.kito.kore.client.gui.kanvas.transform.KvsVec

object Theme {

    val guiBgSpriteLocation get() = kvsSprite("gui_background")
    val guiBg2SpriteLocation get() = kvsSprite("gui_foreground")
    val textAreaSpriteLocation get() = kvsSprite("text_area")
    val textFrameSpriteLocation get() = kvsSprite("text_frame")

    val slotTextureLocation get() = kvsTexture("slot")

    val koreLogoTextureLocation get() = kvsTexture("kore_logo")


    fun KvsNode.guiBackground(transform: KvsTransform) =
        sprite(guiBgSpriteLocation, transform)

    fun KvsNode.guiBackground(pos: KvsVec, scale: KvsVec) =
        sprite(guiBgSpriteLocation, pos, scale)

    fun KvsNode.guiForeground(transform: KvsTransform) =
        sprite(guiBg2SpriteLocation, transform)

    fun KvsNode.guiForeground(pos: KvsVec, scale: KvsVec) =
        sprite(guiBg2SpriteLocation, pos, scale)

    fun KvsNode.textArea(transform: KvsTransform) =
        sprite(textAreaSpriteLocation, transform)

    fun KvsNode.textArea(pos: KvsVec, scale: KvsVec) =
        sprite(textAreaSpriteLocation, pos, scale)

    fun KvsNode.textFrame(transform: KvsTransform) =
        sprite(textFrameSpriteLocation, transform)

    fun KvsNode.textFrame(pos: KvsVec, scale: KvsVec) =
        sprite(textFrameSpriteLocation, pos, scale)

    fun KvsNode.slotBack(transform: KvsTransform) =
        texture(slotTextureLocation, transform)

    fun KvsNode.slotBack(pos: KvsVec, scale: KvsVec) =
        texture(slotTextureLocation, pos, scale)

    fun KvsNode.koreLogo(transform: KvsTransform) =
        texture(koreLogoTextureLocation, transform)

    fun KvsNode.koreLogo(pos: KvsVec, scale: KvsVec) =
        texture(koreLogoTextureLocation, pos, scale)
}