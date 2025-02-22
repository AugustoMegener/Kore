package io.kito.kore.common.reflect

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
annotation class ObjectScanner(val clazz: KClass<*>, val priority: Int = 0) {

    @Scan
    companion object {

        private val objectScanners = hashMapOf<Int, ArrayList<Pair<KClass<*>, KFunction<*>>>>()

        @ClassScanner(Any::class)
        fun scanObjectScanners(info: IModInfo, container: ModContainer, data: KClass<out Any>) {
            data.java.methods.mapNotNull { it.kotlinFunction }.forEach { fn ->
                val scanner = fn.findAnnotation<ObjectScanner>() ?: return@forEach

                objectScanners.computeIfAbsent(scanner.priority) { arrayListOf() } .add(scanner.clazz to fn)
            }
        }

        @ClassScanner(Any::class, priority = 1)
        fun scanObjects(info: IModInfo, container: ModContainer, data: KClass<out Any>) {
            objectScanners.toList()
                .sortedBy { it.first }
                .map      { it.second }
                .forEach  { actual ->
                    for ((cls, fn) in actual) {
                        if (!data.isSubclassOf(cls))     continue
                        val obj = data.objectInstance ?: continue

                        fn.javaMethod!!.declaringClass.kotlin.objectInstance
                            ?.let { fn.call(it, info, container, obj) }
                            ?:run { fn.call(    info, container, obj) }
                    }
                }
        }
    }
}
