/*
 * AloxClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderMC/AloxClient/
 */
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.features.module.modules.visual.XRay
import net.minecraft.block.Block

object XrayCommand : Command("xray") {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            if (args[1].equals("add", ignoreCase = true)) {
                if (args.size > 2) {
                    try {
                        val block = try {
                            Block.getBlockById(args[2].toInt())
                        } catch (exception: NumberFormatException) {
                            val tmpBlock = Block.getBlockFromName(args[2])

                            if (tmpBlock == null || Block.getIdFromBlock(tmpBlock) <= 0) {
                                chat("§7Block §8${args[2]}§7 does not exist!")
                                return
                            }

                            tmpBlock
                        }

                        if (block == null || block in XRay.xrayBlocks) {
                            chat("This block is already on the list.")
                            return
                        }

                        XRay.xrayBlocks += block
                        chat("§7Added block §8${block.localizedName}§7.")
                        playEdit()
                    } catch (exception: NumberFormatException) {
                        chatSyntaxError()
                    }

                    return
                }

                chatSyntax("xray add <block_id>")
                return
            }

            if (args[1].equals("remove", ignoreCase = true)) {
                if (args.size > 2) {
                    try {
                        val block = try {
                            Block.getBlockById(args[2].toInt())
                        } catch (exception: NumberFormatException) {
                            val tmpBlock = Block.getBlockFromName(args[2])

                            if (tmpBlock == null || Block.getIdFromBlock(tmpBlock) <= 0) {
                                chat("§7Block §8${args[2]}§7 does not exist!")
                                return
                            }

                            tmpBlock
                        }

                        if (block == null || block !in XRay.xrayBlocks) {
                            chat("This block is not on the list.")
                            return
                        }

                        XRay.xrayBlocks.remove(block)
                        chat("§7Removed block §8${block.localizedName}§7.")
                        playEdit()
                    } catch (exception: NumberFormatException) {
                        chatSyntaxError()
                    }

                    return
                }
                chatSyntax("xray remove <block_id>")
                return
            }

            if (args[1].equals("list", ignoreCase = true)) {
                chat("§8Xray blocks:")
                XRay.xrayBlocks.forEach { chat("§8${it.localizedName} §7-§c ${Block.getIdFromBlock(it)}") }
                return
            }
        }
        chatSyntax("xray <add, remove, list>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty())
            return emptyList()

        return when (args.size) {
            1 -> {
                arrayOf("add", "remove", "list")
                    .map { it.lowercase() }
                    .filter { it.startsWith(args[0], true) }
            }
            2 -> {
                when (args[0].lowercase()) {
                    "add" -> {
                        return Block.blockRegistry.keys
                            .map { it.resourcePath.lowercase() }
                            .filter { Block.getBlockFromName(it.lowercase()) != null }
                            .filter { Block.getBlockFromName(it.lowercase()) !in XRay.xrayBlocks }
                            .filter { it.startsWith(args[1], true) }
                    }
                    "remove" -> {
                        return Block.blockRegistry.keys
                            .map { it.resourcePath.lowercase() }
                            .filter { Block.getBlockFromName(it) in XRay.xrayBlocks }
                            .filter { it.startsWith(args[1], true) }
                    }
                    else -> emptyList()
                }

            }
            else -> emptyList()
        }
    }
}