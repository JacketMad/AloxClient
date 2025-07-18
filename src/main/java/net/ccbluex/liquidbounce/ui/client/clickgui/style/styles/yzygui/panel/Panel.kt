/*
 * AloxClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderMC/AloxClient/
 */
package net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.yzygui.panel

import net.ccbluex.liquidbounce.AloxClient.customFontManager
import net.ccbluex.liquidbounce.AloxClient.guiManager
import net.ccbluex.liquidbounce.AloxClient.moduleManager
import net.ccbluex.liquidbounce.features.module.modules.client.ClickGUIModule
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.yzygui.category.yzyCategory
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.yzygui.panel.element.impl.ModuleElement
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.yzygui.yzyGUI
import net.ccbluex.liquidbounce.utils.attack.CPSCounter.isHovering
import net.ccbluex.liquidbounce.utils.render.Pair
import net.ccbluex.liquidbounce.utils.render.RenderUtils.yzyRectangle
import net.ccbluex.liquidbounce.utils.render.RenderUtils.yzyTexture
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.util.*

/**
 * @author opZywl - YZY GUI
 */
class Panel(
    val parent: yzyGUI,
    val category: yzyCategory,
    var x: Int,
    var y: Int
) {
    private val elements: MutableList<ModuleElement> = moduleManager.getModuleInCategory(category.parent).mapIndexed { index, module ->
        ModuleElement(module, this, x + 1, PANEL_HEIGHT + 1 + index * ModuleElement.MODULE_HEIGHT, PANEL_WIDTH - 2, ModuleElement.MODULE_HEIGHT)
    }.toMutableList()

    var width: Int = PANEL_WIDTH
    var height: Int = PANEL_HEIGHT

    private var dragged: Int = 0
    private var lastX: Int = 0
    private var lastY: Int = 0

    var open: Boolean = false

    private var fade: Float = 0f
    private var isDragging: Boolean = false
    var isExtended: Boolean = false

    private val elementsHeight = 0f

    fun isHovering(mouseX: Int, mouseY: Int): Boolean {
        return isHovering(mouseX, mouseY, x, y, x + width, y + height)
    }

    fun handleScroll(mouseX: Int, mouseY: Int, wheel: Int): Boolean {
        val maxElements = moduleManager[ClickGUIModule::class.java.simpleName]?.values?.find { it.name == "MaxElements" }?.get() as? Int ?: 0

        if (mouseX in x..(x + 100) && mouseY in y..(y + 19 + elementsHeight.toInt())) {
            when {
                wheel < 0 && dragged < elements.size - maxElements -> dragged++
                wheel > 0 && dragged > 0 -> dragged--
            }
            return true
        }
        return false
    }

    fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (isDragging) {
            x = mouseX + lastX
            y = mouseY + lastY
        }


        var panelHeight = height.toFloat()

        for (element in elements) {
            if (isExtended) {
                panelHeight += element.height.toFloat()
            }

            panelHeight += element.getExtendedHeight()
        }

        yzyRectangle(x - 0.5f, y - 0.5f, width + 1.0f, panelHeight + 3.0f, category.color)
        yzyRectangle(x.toFloat(), y.toFloat(), width.toFloat(), panelHeight + 2.0f, Color(26, 26, 26))

        customFontManager["lato-bold-15"]?.drawStringWithShadow(
            category.name.lowercase(Locale.getDefault()),
            (x + 3).toDouble(),
            (y + (height / 4.0f) + 0.5f).toDouble(),
            -1
        )

        pushMatrix()
        enableAlpha()
        enableBlend()

        Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation("fdpclient/clickgui/zywl/icons/eye.png"))
        val size = height - 7
        yzyTexture(
            (x + width - size * 2 - 7).toDouble(),
            (y + (height / 4.0f)).toDouble(),
            0.0f, 0.0f, size.toDouble(), size.toDouble(), size.toFloat(), size.toFloat(), category.color
        )

        Minecraft.getMinecraft().textureManager.bindTexture(category.getIcon())
        yzyTexture(
            (x + width - size - 3).toDouble(),
            (y + (height / 4.0f)).toDouble(),
            0.0f, 0.0f, size.toDouble(), size.toDouble(), size.toFloat(), size.toFloat(), category.color
        )

        disableBlend()
        disableAlpha()
        popMatrix()

        if (isExtended) {
            var addition = height
            elements.forEach { element ->
                element.x = x + 1
                element.y = y + addition
                element.drawScreen(mouseX, mouseY, partialTicks)
                addition += element.height + if (element.isExtended) element.getExtendedHeight().toInt() else 0
            }
        }
    }

    fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        if (isHovering(mouseX, mouseY)) {
            when (button) {
                0 -> {
                    isDragging = true
                    lastX = x - mouseX
                    lastY = y - mouseY
                }
                1 -> isExtended = !isExtended
            }
        }

        if (isExtended) elements.forEach { it.mouseClicked(mouseX, mouseY, button) }
    }

    fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        isDragging = false
        if (isExtended) elements.forEach { it.mouseReleased(mouseX, mouseY, state) }
    }

    fun keyTyped(character: Char, code: Int) {
        if (isExtended) elements.forEach { it.keyTyped(character, code) }
    }

    fun updateFade(delta: Int) {
        fade = when {
            open && fade < elementsHeight -> fade + 0.4f * delta
            !open && fade > 0 -> fade - 0.4f * delta
            else -> fade
        }.coerceIn(0f, elementsHeight)
    }

    fun onGuiClosed() {
        guiManager.positions[category] = Pair(x, y)
        guiManager.extendeds[category] = isExtended
    }

    companion object {
        const val PANEL_WIDTH: Int = 100
        const val PANEL_HEIGHT: Int = 15
    }
}