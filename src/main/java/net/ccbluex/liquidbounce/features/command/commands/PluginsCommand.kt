/*
 * AloxClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderMC/AloxClient/
 */
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.features.module.modules.exploit.Plugins

object PluginsCommand : Command("plugins") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        Plugins.toggle()
    }
}