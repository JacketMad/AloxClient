/*
 * AloxClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderMC/AloxClient/
 */
package net.ccbluex.liquidbounce.ui.client.gui

import net.ccbluex.liquidbounce.features.module.modules.client.BrandSpoofer.possibleBrands
import net.ccbluex.liquidbounce.features.module.modules.client.HUDModule.guiColor
import net.ccbluex.liquidbounce.handler.payload.ClientFixes.blockFML
import net.ccbluex.liquidbounce.handler.payload.ClientFixes.blockPayloadPackets
import net.ccbluex.liquidbounce.handler.payload.ClientFixes.blockProxyPacket
import net.ccbluex.liquidbounce.handler.payload.ClientFixes.blockResourcePackExploit
import net.ccbluex.liquidbounce.handler.payload.ClientFixes.fmlFixesEnabled
import net.ccbluex.liquidbounce.file.FileManager.saveConfig
import net.ccbluex.liquidbounce.file.FileManager.valuesConfig
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer.Companion.assumeNonVolatile
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawBloom
import net.ccbluex.liquidbounce.utils.ui.AbstractScreen
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.io.IOException
import java.util.*

class GuiClientFixes(private val prevGui: GuiScreen) : AbstractScreen() {

    private lateinit var enabledButton: GuiButton
    private lateinit var fmlButton: GuiButton
    private lateinit var proxyButton: GuiButton
    private lateinit var payloadButton: GuiButton
    private lateinit var customBrandButton: GuiButton
    private lateinit var resourcePackButton: GuiButton

    override fun initGui() {
        enabledButton = +GuiButton(
            1,
            width / 2 - 100,
            height / 4 + 35,
            "AntiForge (" + (if (fmlFixesEnabled) "On" else "Off") + ")"
        )
        fmlButton =
            +GuiButton(2, width / 2 - 100, height / 4 + 35 + 25, "Block FML (" + (if (blockFML) "On" else "Off") + ")")
        proxyButton = +GuiButton(
            3,
            width / 2 - 100,
            height / 4 + 35 + 25 * 2,
            "Block FML Proxy Packet (" + (if (blockProxyPacket) "On" else "Off") + ")"
        )
        payloadButton = +GuiButton(
            4,
            width / 2 - 100,
            height / 4 + 35 + 25 * 3,
            "Block Non-MC Payloads (" + (if (blockPayloadPackets) "On" else "Off") + ")"
        )
        customBrandButton = +GuiButton(5, width / 2 - 100, height / 4 + 35 + 25 * 4, "Brand (${possibleBrands.get()})")
        resourcePackButton = +GuiButton(
            6,
            width / 2 - 100,
            height / 4 + 50 + 25 * 5,
            "Block Resource Pack Exploit (" + (if (blockResourcePackExploit) "On" else "Off") + ")"
        )
        +GuiButton(0, width / 2 - 100, height / 4 + 55 + 25 * 6 + 5, "Back")
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            1 -> {
                fmlFixesEnabled = !fmlFixesEnabled
                enabledButton.displayString = "AntiForge (${if (fmlFixesEnabled) "On" else "Off"})"
            }
            2 -> {
                blockFML = !blockFML
                fmlButton.displayString = "Block FML (${if (blockFML) "On" else "Off"})"
            }
            3 -> {
                blockProxyPacket = !blockProxyPacket
                proxyButton.displayString = "Block FML Proxy Packet (${if (blockProxyPacket) "On" else "Off"})"
            }
            4 -> {
                blockPayloadPackets = !blockPayloadPackets
                payloadButton.displayString = "Block Non-MC Payloads (${if (blockPayloadPackets) "On" else "Off"})"
            }
            5 -> {
                val brands = possibleBrands.values

                // Switch to next client brand
                val currentBrand = possibleBrands.get()
                val nextBrand = brands[(brands.indexOf(currentBrand) + 1) % brands.size]
                possibleBrands.set(nextBrand)

                customBrandButton.displayString = "Brand ($nextBrand)"
            }
            6 -> {
                blockResourcePackExploit = !blockResourcePackExploit
                resourcePackButton.displayString = "Block Resource Pack Exploit (${if (blockResourcePackExploit) "On" else "Off"})"
            }
            0 -> mc.displayGuiScreen(prevGui)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {

        assumeNonVolatile = true

        drawBackground(0)
        Fonts.fontBold180.drawCenteredString("Fixes", width / 2f, height / 8f + 5f, 4673984, true)

        drawBloom(mouseX - 5, mouseY - 5, 10, 10, 16, Color(guiColor))

        assumeNonVolatile = false

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    @Throws(IOException::class)
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }

    override fun onGuiClosed() {
        saveConfig(valuesConfig)
        super.onGuiClosed()
    }
}
