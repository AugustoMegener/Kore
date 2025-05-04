package io.kito.kore_tests.server.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.kito.kore.common.reflect.Scan
import io.kito.kore.server.command.RegisterCommand
import io.kito.kore.util.minecraft.*
import io.kito.kore.util.minecraft.ResourceLocationExt.toLoc
import io.kito.kore_tests.common.resource.NiceDataReloadListener
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.MessageArgument.Message
import net.minecraft.commands.arguments.MessageArgument.message

@Scan
object Commands {

    @RegisterCommand
    fun myNiceCommand(): LiteralArgumentBuilder<CommandSourceStack> =
        literal("hello") + {
            then(literal("world") + {
                runs { source.sendSuccess({ "Hello, world!".literal }, true); 1 }
            })
            then(literal("data") + {
                then(literal("all") + {
                    runs {
                        source.sendSuccess({
                            "".literal.apply {
                                NiceDataReloadListener.values.forEach { data ->
                                    append(data.toString() + "\n")
                                }
                            }
                        }, true); 1
                    }
                })
                then(argument("id", message()) + {
                    runs {
                        source.sendSuccess(
                            { NiceDataReloadListener.entries[arg<Message>("id").text.toLoc()]!!.toString().literal },
                            true
                        )
                        1
                    }
                })
            })
        }
}