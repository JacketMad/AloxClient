/*
 * AloxClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderMC/AloxClient/
 */
package net.ccbluex.liquidbounce.handler.macro

import net.ccbluex.liquidbounce.AloxClient.commandManager

class Macro(val key: Int, val command: String) {
    fun exec() {
        commandManager.executeCommands(command)
    }
}