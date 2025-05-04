package io.kito.kore.server.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.kito.kore.common.event.KSubscribe
import io.kito.kore.common.reflect.FunScanner
import io.kito.kore.common.reflect.FunScanner.Companion.globalBound
import io.kito.kore.common.reflect.Scan
import io.kito.kore.util.UNCHECKED_CAST
import net.minecraft.commands.CommandSourceStack
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforgespi.language.IModInfo
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaMethod

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterCommand {

    @Scan
    companion object : FunScanner<LiteralArgumentBuilder<*>> {
        private val commands = arrayListOf<() -> LiteralArgumentBuilder<CommandSourceStack>>()

        override val bound = globalBound
        override val annotation = RegisterCommand::class
        override val returnType = LiteralArgumentBuilder::class

        override fun validateParameters(parms: List<KParameter>) = parms.size == 1

        @Suppress(UNCHECKED_CAST)
        override fun use(info: IModInfo, container: ModContainer, data: KFunction<LiteralArgumentBuilder<*>>) {
            commands += { data.call(data.javaMethod!!.declaringClass.kotlin.objectInstance) }
                    as () -> LiteralArgumentBuilder<CommandSourceStack>
        }

        @KSubscribe
        fun RegisterCommandsEvent.onRegisterCommands() {
            commands.forEach { dispatcher.register(it()) }
        }
    }
}
