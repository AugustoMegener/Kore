package io.kito.kore.client.gui.kanvas.transform

import io.kito.kore.client.gui.kanvas.node.KvsNode
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.height
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.width
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.x
import io.kito.kore.client.gui.kanvas.node.KvsNode.Companion.y

interface KvsVec {

    fun x(obj:  KvsNode): Int
    fun y(obj:  KvsNode): Int

    @ConsistentCopyVisibility
    data class Absolute private constructor(val x: Int, val y: Int) : KvsVec {

        override fun x(obj: KvsNode) = x
        override fun y(obj: KvsNode) = y

        companion object {
            val start = Absolute(0, 0)
            val slotSize = Absolute(16, 16)

            fun px(value: Int) = Absolute(value, value)
            fun px(x: Int, y: Int) = Absolute(x, y)

            fun KvsNode.lh(value: Int) = px(0, font.lineHeight * value)
            fun KvsNode.cw(value: Int) = px(font.width("M") * value, 0)
        }
    }

    @ConsistentCopyVisibility
    data class Relative private constructor(val x: Float, val y: Float) : KvsVec {

        override fun x(obj: KvsNode) = (obj.parent.width * x).toInt()
        override fun y(obj: KvsNode) = (obj.parent.height * y).toInt()

        companion object {
            val fill = Relative(1f, 1f)

            fun pt(value: Float) = Relative(value, value)
            fun pt(x: Float, y: Float) = Relative(x, y)
        }
    }

    @ConsistentCopyVisibility
    data class Sum private constructor(val a: KvsVec, val b: KvsVec) : KvsVec {

        override fun x(obj: KvsNode) = a.x(obj) + b.x(obj)
        override fun y(obj: KvsNode) = a.y(obj) + b.y(obj)

        companion object {
            operator fun KvsVec.plus(other: KvsVec) = Sum(this, other)
        }
    }

    @ConsistentCopyVisibility
    data class Sub private constructor(val a: KvsVec, val b: KvsVec) : KvsVec {

        override fun x(obj: KvsNode) = a.x(obj) - b.x(obj)
        override fun y(obj: KvsNode) = a.y(obj) - b.y(obj)

        companion object {
            operator fun KvsVec.minus(other: KvsVec) = Sub(this, other)
        }
    }

    class Function private constructor(val x: KvsNode.() -> Int, val y:  KvsNode.() -> Int) : KvsVec {

        override fun x(obj: KvsNode) = obj.x()
        override fun y(obj: KvsNode) = obj.y()

        companion object {
            fun fn(value:  KvsNode.() -> Int) = Function(value, value)
            fun fn(x:  KvsNode.() -> Int, y:  KvsNode.() -> Int) = Function(x, y)
        }
    }

    data object Centered : KvsVec {

        override fun x(obj:  KvsNode) = (obj.parent.width - obj.width) / 2
        override fun y(obj:  KvsNode) = (obj.parent.height - obj.height) / 2
    }

    data object End : KvsVec {

        override fun x(obj: KvsNode) = (obj.parent.width - obj.width)
        override fun y(obj: KvsNode) = (obj.parent.height - obj.height)
    }

    data object AfterLast : KvsVec {

        override fun x(obj: KvsNode) = obj.brotherHEnd
        override fun y(obj: KvsNode) = obj.brotherVEnd
    }

    data object Expand : KvsVec {

        override fun x(obj: KvsNode) =
            obj.parent.width - ((obj.brotherAfter?.x ?: 0) + (obj.brotherAfter?.width ?: 0)) - obj.brotherHEnd
        override fun y(obj: KvsNode) =
            obj.parent.height - ((obj.brotherAfter?.y ?: 0) + (obj.brotherAfter?.height ?: 0)) - obj.brotherVEnd

    }

    data object FitChildren : KvsVec {

        override fun x(obj: KvsNode) = obj.children.maxOfOrNull { it.x + it.width } ?: 0
        override fun y(obj: KvsNode) = obj.children.maxOfOrNull { it.x + it.width } ?: 0
    }

    @ConsistentCopyVisibility
    data class Compound private constructor(val xSource: KvsVec, val ySource: KvsVec)
        : KvsVec
    {
        override fun x(obj: KvsNode) = xSource.x(obj)
        override fun y(obj: KvsNode) = ySource.y(obj)

        companion object {
            fun xy(x: KvsVec, y: KvsVec) = Compound(x, y)
        }
    }

    companion object {
        val KvsNode.indexInParent get() = parent.children.indexOf(this)
        val KvsNode.brotherBefore get() = parent.children.getOrNull(indexInParent-1)
        val KvsNode.brotherAfter get() = parent.children.getOrNull(indexInParent+1)

        val KvsNode.brotherHEnd get() = (brotherBefore?.x ?: 0) + (brotherBefore?.width  ?: 0)
        val KvsNode.brotherVEnd get() = (brotherBefore?.y ?: 0) + (brotherBefore?.height ?: 0)
    }
}