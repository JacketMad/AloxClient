/*
 * AloxClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderMC/AloxClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.AloxClient.discordRPC
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.Module
object DiscordRPCModule : Module("DiscordRPC", Category.CLIENT) {

    val showServerValue by boolean("ShowServer", false)
    val showNameValue by boolean("ShowName", true)
    val showHealthValue by boolean("ShowHealth", false)
    val showOtherValue by boolean("ShowOther", false)
    val showModuleValue by boolean("ShowModule", false)
    val animated by boolean("ShouldAnimate", true)

    override fun onEnable() {
        discordRPC.run()
    }

    override fun onDisable() {
        discordRPC.stop()
    }
}
