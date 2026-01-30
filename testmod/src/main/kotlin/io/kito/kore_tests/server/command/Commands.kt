package io.kito.kore_tests.server.command

import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import io.kito.kore.common.reflect.Scan
import io.kito.kore.server.command.RegisterCommand
import io.kito.kore.util.minecraft.ResourceLocationExt.toLoc
import io.kito.kore.util.minecraft.arg
import io.kito.kore.util.minecraft.literal
import io.kito.kore.util.minecraft.plus
import io.kito.kore.util.minecraft.runs
import io.kito.kore_tests.common.resource.NiceDataReloadListener.Companion.niceDataReloadListener
import io.kito.kore_tests.common.resource.NiceDslScriptReloadListener
import io.kito.kore_tests.common.resource.NiceScriptReloadListener
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.MessageArgument.Message
import net.minecraft.commands.arguments.MessageArgument.message
import net.minecraft.commands.arguments.ResourceLocationArgument.id
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation

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
                                niceDataReloadListener.values.forEach { data ->
                                    append(data.toString() + "\n")
                                }
                            }
                        }, true); 1
                    }
                })
                then(argument("id", message()) + {
                    runs {
                        source.sendSuccess(
                            { niceDataReloadListener.entries[arg<Message>("id").text.toLoc()]!!.toString().literal },
                            true
                        )
                        1
                    }
                })
            })
        }

    @RegisterCommand
    fun myCalcCommand(): LiteralArgumentBuilder<CommandSourceStack> =
        literal("calc") + {
            then(argument("id", id()) + {
                then(argument("x", integer()) + {
                    then(argument("y", integer()) + {
                        runs {
                            source.sendSuccess(
                                {
                                    val a = arg<ResourceLocation>("id")
                                    (NiceScriptReloadListener.lambdas[a]
                                        ?: throw CommandSyntaxException(null, "no ${arg<ResourceLocation>("id")} operator".literal)
                                            )(arg("x"), arg("y")).toString().literal
                                }, true
                            )
                            1
                        }
                    })
                })
            })
        }

    @RegisterCommand
    fun myReadCommand(): LiteralArgumentBuilder<CommandSourceStack> =
        literal("read") + {
            then(argument("id", id()) + {
                runs {
                    val text = (NiceDslScriptReloadListener.texts[arg<ResourceLocation>("id")]
                        ?: throw CommandSyntaxException(null, "no ${arg<ResourceLocation>("id")} text".literal))


                    source.sendSuccess(
                        {
                            text.title
                                .withColor(0xfabd05)
                                .withStyle(Style.EMPTY.withBold(true))
                                .append("\n\n")
                                .apply { text.lines.forEach { append(it.append("\n").withStyle(Style.EMPTY)) } }
                        },
                        true
                    )
                    1
                }
            })
        }
}