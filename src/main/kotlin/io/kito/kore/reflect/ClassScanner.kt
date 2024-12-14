package io.kito.kore.reflect

import io.kito.kore.util.*
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
annotation class ClassScanner(val clazz: KClass<*>, val bound: Bound.Type = Bound.Type.GLOBAL, val priority: Int = 0) {

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

            forEachKoreUserFile {
                scanResult.classes.flatMap { c -> c.clazz.clazz.methods.mapNotNull { it.kotlinFunction } }
                    .forEach { fn ->
                        val scanner = fn.findAnnotation<ClassScanner>() ?: return@forEach

                        scanners.computeIfAbsent(scanner.priority) { arrayListOf() } +=
                            ClassScannerData(scanner.clazz, fn, Bound(scanner.bound, info.modId))
                    }
            }

            scanners.toList().sortedBy { it.first }.forEach { (_, scanners) ->
                scanners.forEach { data ->
                    forEachKoreUserFile {
                        for (cls in scanResult.classes.map { it.clazz.klass }) {
                            if (!cls.isSubclassOf(data.target)) return@forEachKoreUserFile

                            scan(data, cls, info, modContainer)
                        }
                    }
                }
            }
        }
    }
}
