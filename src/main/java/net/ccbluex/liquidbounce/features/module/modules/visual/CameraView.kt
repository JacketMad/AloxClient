/*
 * AloxClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderMC/AloxClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.visual

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.modules.player.scaffolds.Scaffold

object CameraView : Module("CameraView", Category.VISUAL) {

    val clip by boolean("Clip", true)

    private val view by boolean("View", true)
    private val customY by float("CustomY", 0f, -10f..10f) { view }
    private val saveLastGroundY by boolean("SaveLastGroundY", true)  { view }
    private val onScaffold by boolean("OnScaffold", true)  { view }
    private val onF5 by boolean("OnF5", true)  { view }

  //  val fovValue by float("FOV", 1f, 0f.. 30f)

    private var launchY: Double ?= null
    override fun onEnable() {
        if (view) {
            mc.thePlayer?.run {
                launchY = posY
            }
        }
    }

    val onMotion = handler<MotionEvent> { event ->
        if (view) {
            if (event.eventState != EventState.POST) return@handler
            mc.thePlayer?.run {
                if (!saveLastGroundY || (onGround || ticksExisted == 1)) {
                    launchY = posY
                }
            }
        }
    }
    val onCameraUpdate = handler<CameraPositionEvent> { event ->
        mc.thePlayer?.run {
            val currentLaunchY = launchY ?: return@handler
            if (onScaffold && !Scaffold.handleEvents()) return@handler
            if (onF5 && mc.gameSettings.thirdPersonView == 0) return@handler
            event.withY(currentLaunchY + customY)
        }
    }
}