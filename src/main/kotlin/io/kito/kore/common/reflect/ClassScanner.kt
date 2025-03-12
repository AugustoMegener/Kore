package io.kito.kore.common.reflect

import io.kito.kore.common.reflect.Scan.Companion.scaneables
import io.kito.kore.util.Bound
import io.kito.kore.util.neoforge.Mods.forEachModContainer
import net.neoforged.fml.ModContainer
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ClassScanner(val clazz    : KClass<*>,
                              val bound    : Bound.Type = Bound.Type.GLOBAL,
                              val priority : Int = 0)
{
    companion object {

        private data class ClassScannerData<T : Any>(val target: KClass<T>, val kFun: KFunction<*>, val bound: Bound)

        private fun scan(data: ClassScannerData<*>, toScan: KClass<*>, modInfo: IModInfo, container: ModContainer) {
            if (data.bound.isOnBound(modInfo.modId))
                data.kFun.javaMethod!!.declaringClass.kotlin.objectInstance
                    ?. let { data.kFun.call(it, modInfo, container, toScan) }
                    ?: run { data.kFun.call(    modInfo, container, toScan) }
        }

        fun scanClasses() {
            val scanners: HashMap<Int, ArrayList<ClassScannerData<*>>> = hashMapOf()


            for ((id, clss) in scaneables) {
                clss.flatMap    { it.java.methods.toList() }
                    .mapNotNull { it.kotlinFunction }
                    .forEach    {
                        with(it.findAnnotation<ClassScanner>() ?: return@forEach) {
                            scanners.computeIfAbsent(priority) { arrayListOf() } +=
                                ClassScannerData(clazz, it, Bound(bound, id))
                        }
                    }
            }

            scanners.toList().sortedBy { it.first }.forEach { (_, scanners) ->
                scanners.forEach { data ->
                    forEachModContainer { id ->
                        for (cls in scaneables[id] ?: return@forEachModContainer)
                            { if (cls.isSubclassOf(data.target)) scan(data, cls, modInfo, this) }
                    }
                }
            }
        }
    }
}
