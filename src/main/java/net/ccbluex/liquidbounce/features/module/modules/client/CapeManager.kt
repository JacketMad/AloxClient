/*
 * AloxClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderMC/AloxClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.ui.client.gui.GuiCapeManager

object CapeManager : Module("CapeManager", Category.CLIENT, canBeEnabled = false) {
    override fun onEnable() {
        mc.displayGuiScreen(GuiCapeManager)
    }
}