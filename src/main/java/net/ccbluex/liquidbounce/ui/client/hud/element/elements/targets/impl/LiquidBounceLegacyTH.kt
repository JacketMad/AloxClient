/*
 * AloxClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderMC/AloxClient/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.impl

import net.ccbluex.liquidbounce.config.*
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Targets
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.TargetStyle
import net.ccbluex.liquidbounce.utils.attack.EntityUtils
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils.deltaTime
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawRect
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawRoundedBorderRect
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawScaledCustomSizeModalRect
import net.ccbluex.liquidbounce.utils.render.animation.AnimationUtil
import net.ccbluex.liquidbounce.utils.render.animation.AnimationUtil.debugFPS
import net.ccbluex.liquidbounce.utils.render.shader.shaders.RainbowShader
import net.minecraft.client.gui.GuiChat
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

class LiquidBounceLegacyTH(inst: Targets) : TargetStyle("LiquidBounce", inst, true) {

    private val roundedRectRadius by FloatValue("Rounded-Radius", 3F, 0F..5F)

    private val borderStrength by FloatValue("Border-Strength", 3F, 1F..5F)

    private val backgroundMode by ListValue("Background-Color", arrayOf("Custom", "Rainbow"), "Custom")
    private val backgroundRed by IntValue("Background-R", 0, 0..255).apply {
        setSupport { backgroundMode == "Custom" } }
    private val backgroundGreen by IntValue("Background-G", 0, 0..255).apply {
        setSupport { backgroundMode == "Custom" } }
    private val backgroundBlue by IntValue("Background-B", 0, 0..255).apply {
        setSupport { backgroundMode == "Custom" } }
    private val backgroundAlpha by IntValue("Background-Alpha", 255, 0..255).apply {
        setSupport { backgroundMode == "Custom" } }

    private val borderMode by ListValue("Border-Color", arrayOf("Custom", "Rainbow"), "Custom")
    private val borderRed by IntValue("Border-R", 0, 0..255).apply {
        setSupport { borderMode == "Custom" } }
    private val borderGreen by IntValue("Border-G", 0, 0..255).apply {
        setSupport { borderMode == "Custom" } }
    private val borderBlue by IntValue("Border-B", 0, 0..255).apply {
        setSupport { borderMode == "Custom" } }
    private val borderAlpha by IntValue("Border-Alpha", 255, 0..255).apply {
        setSupport { borderMode == "Custom" } }

    private val textRed by IntValue("Text-R", 255, 0..255)
    private val textGreen by IntValue("Text-G", 255, 0..255)
    private val textBlue by IntValue("Text-B", 255, 0..255)
    private val textAlpha by IntValue("Text-Alpha", 255, 0..255)

    private val rainbowX by FloatValue("Rainbow-X", -1000F, -2000F..2000F).apply {
        setSupport { backgroundMode == "Rainbow" } }
    private val rainbowY by FloatValue("Rainbow-Y", -1000F, -2000F..2000F).apply {
        setSupport { backgroundMode == "Rainbow" } }

    private val titleFont by FontValue("TitleFont", Fonts.fontSemibold40)
    private val bodyFont by FontValue("BodyFont", Fonts.fontSemibold35)
    private val textShadow by BoolValue("TextShadow", false)

    private val fadeSpeed by FloatValue("FadeSpeed", 2F, 1F..9F)
    private val absorption by BoolValue("Absorption", true)
    private val healthFromScoreboard by BoolValue("HealthFromScoreboard", true)

    private val animation by ListValue("Animation", arrayOf("Smooth", "Fade"), "Fade")
    private val animationSpeed by FloatValue("AnimationSpeed", 0.2F, 0.05F..1F)
    private val vanishDelay by IntValue("VanishDelay", 300, 0..500)

    private var lastTarget: EntityLivingBase? = null

    private var width = 0f
    private var height = 0f

    private val isRendered: Boolean
        get() = width > 0f || height > 0f

    private var alphaText = 0
    private var alphaBackground = 0
    private var alphaBorder = 0

    private val isAlpha: Boolean
        get() = alphaBorder > 0 || alphaBackground > 0 || alphaText > 0

    private var delayCounter = 0

    override fun drawTarget(entity: EntityLivingBase) {
        val target = KillAura.target ?: if (delayCounter >= vanishDelay) mc.thePlayer else lastTarget ?: mc.thePlayer
        val shouldRender = (KillAura.handleEvents() && KillAura.target != null || mc.currentScreen is GuiChat)
        val smoothMode = animation == "Smooth"
        val fadeMode = animation == "Fade"

        val stringWidth = (40f + (target.name?.let(titleFont::getStringWidth) ?: 0)).coerceAtLeast(118F)

        if (shouldRender) {
            delayCounter = 0
        } else if (isRendered || isAlpha) {
            delayCounter++
        }

        if (smoothMode && !shouldRender && delayCounter >= vanishDelay) {
            val decrement = (animationSpeed / (debugFPS / 60)).coerceAtLeast(0f)
            width -= decrement
            height -= decrement
        }

        if (shouldRender || isRendered || isAlpha) {
            val targetHealth = EntityUtils.getHealth(target!!, healthFromScoreboard, absorption)
            val maxHealth = target.maxHealth + if (absorption) target.absorptionAmount else 0F

            // Calculate health color based on entity's health
            val healthColor = when {
                targetHealth <= 0 -> Color(255, 0, 0, if (fadeMode) alphaText else textAlpha)
                else -> {
                    ColorUtils.interpolateHealthColor(
                        target,
                        255, 255, 0,
                        if (fadeMode) alphaText else textAlpha,
                        healthFromScoreboard,
                        absorption
                    )
                }
            }

            if (target != lastTarget || easingHealth < 0 || easingHealth > maxHealth || abs(easingHealth - targetHealth) < 0.01) {
                easingHealth = targetHealth
            }

            if (smoothMode) {
                val targetWidth = if (shouldRender) stringWidth else if (delayCounter >= vanishDelay) 0f else width

                width = AnimationUtil.base(width.toDouble(), targetWidth.toDouble(), animationSpeed.toDouble()).toFloat()
                    .coerceAtLeast(0f)

                val targetHeight = if (shouldRender) 40f else if (delayCounter >= vanishDelay) 0f else height
                height = AnimationUtil.base(height.toDouble(), targetHeight.toDouble(), animationSpeed.toDouble()).toFloat()
                    .coerceAtLeast(0f)
            } else {
                width = stringWidth
                height = 40f

                val targetText = if (shouldRender) textAlpha else if (delayCounter >= vanishDelay) 0f else alphaText
                alphaText = AnimationUtil.base(alphaText.toDouble(), targetText.toDouble(), animationSpeed.toDouble()).roundToInt()

                val targetBackground = if (shouldRender) {
                    backgroundAlpha
                } else if (delayCounter >= vanishDelay) {
                    0f
                } else alphaBackground

                alphaBackground = AnimationUtil.base(alphaBackground.toDouble(), targetBackground.toDouble(), animationSpeed.toDouble()).roundToInt()

                val targetBorder = if (shouldRender) {
                    borderAlpha
                } else if (delayCounter >= vanishDelay) {
                    0f
                } else alphaBorder

                alphaBorder = AnimationUtil.base(alphaBorder.toDouble(), targetBorder.toDouble(), animationSpeed.toDouble()).roundToInt()
            }

            val backgroundCustomColor = Color(backgroundRed, backgroundGreen, backgroundBlue, if (fadeMode) alphaBackground else backgroundAlpha).rgb
            val borderCustomColor = Color(
                borderRed, borderGreen, borderBlue, if (fadeMode) {
                    alphaBorder
                } else borderAlpha
            ).rgb

            val textCustomColor = Color(textRed, textGreen, textBlue, if (fadeMode) alphaText else textAlpha).rgb

            val rainbowOffset = System.currentTimeMillis() % 10000 / 10000F
            val rainbowX = if (rainbowX == 0f) 0f else 1f / rainbowX
            val rainbowY = if (rainbowY == 0f) 0f else 1f / rainbowY

            glPushMatrix()

            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            if (fadeMode && shouldRender || smoothMode && shouldRender && width == width || delayCounter < vanishDelay) {
                // Draw rect box
                RainbowShader.begin(backgroundMode == "Rainbow", rainbowX, rainbowY, rainbowOffset).use {
                    drawRoundedBorderRect(
                        0F, 0F, width, height, borderStrength,
                        if (backgroundMode == "Rainbow") 0 else backgroundCustomColor,
                        borderCustomColor,
                        roundedRectRadius
                    )
                }

                // Health bar
                val healthBarWidth = (targetHealth / maxHealth).coerceIn(0F, 1F) * (width - 6f)
                drawRect(3F, 34F, 3f + healthBarWidth, 36F, healthColor.rgb)

                // Easing health update
                easingHealth += ((targetHealth - easingHealth) / 2f.pow(10f - fadeSpeed)) * deltaTime
                val easingHealthWidth = (easingHealth / maxHealth) * (width - 6f)

                // Heal animation, only animate from the right side
                if (easingHealth < targetHealth) {
                    drawRect(3f + easingHealthWidth, 34F, 3f + healthBarWidth, 36F, Color(44, 201, 144).rgb)
                }

                // Damage animation, only animate from the right side
                if (easingHealth > targetHealth) {
                    drawRect(3f + healthBarWidth, 34F, 3f + easingHealthWidth, 36F, Color(252, 185, 65).rgb)
                }

                // Draw title text
                target.name?.let { titleFont.drawString(it, 36F, 5F, textCustomColor, textShadow) }

                // Draw body text
                bodyFont.drawString(
                    "Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))}",
                    36F,
                    15F,
                    textCustomColor,
                    textShadow
                )

                // Draw info
                mc.netHandler?.getPlayerInfo(target.uniqueID)?.let {
                    bodyFont.drawString(
                        "Ping: ${it.responseTime.coerceAtLeast(0)}",
                        36F,
                        24F,
                        textCustomColor,
                        textShadow
                    )

                    // Draw head
                    val locationSkin = it.locationSkin
                    drawHead(locationSkin, 30, 30)
                }
            }

            glPopMatrix()
        }

        lastTarget = target
    }

    private fun drawHead(skin: ResourceLocation?, width: Int, height: Int) {
        val texture: ResourceLocation = skin ?: mc.thePlayer.locationSkin

        glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(texture)
        drawScaledCustomSizeModalRect(4, 4, 8F, 8F, 8, 8, width - 2, height - 2, 64F, 64F)
    }

    override fun getBorder(entity: EntityLivingBase?): Border {
        entity ?: return Border(0F, 0F, 116F, 40F)

        return Border(0F, 0F, 116F, 40F)
    }
}