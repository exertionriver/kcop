package river.exertion.kcop.sim.colorPalette

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import river.exertion.kcop.messaging.Id
import river.exertion.kcop.messaging.MessageChannel
import river.exertion.kcop.messaging.MessageChannelHandler
import river.exertion.kcop.plugin.IDisplayPackage
import river.exertion.kcop.sim.colorPalette.messaging.ColorPaletteMessage
import river.exertion.kcop.sim.colorPalette.view.ColorPaletteInputProcessor
import river.exertion.kcop.sim.colorPalette.view.ColorPaletteLayout

class ColorPalettePackage : IDisplayPackage {

    override var id = Id.randomId()

    override var name = this::class.simpleName.toString()

    override fun loadAssets(assetManager: AssetManager) {} //none needed

    override fun loadChannels() {
        MessageChannelHandler.addChannel(MessageChannel(ColorPaletteBridge, ColorPaletteMessage::class))
    }

    lateinit var colorPaletteLayout : ColorPaletteLayout

    override fun build(screenWidth : Float, screenHeight : Float, stage : Stage) {
        return ColorPaletteLayout(screenWidth, screenHeight).run { this.build(stage) }
    }

    override fun displayKcopScreen(offset : Vector2) = colorPaletteLayout.kcopScreen(offset)

    override fun displayFullScreen(offset : Vector2) = colorPaletteLayout.fullScreen(offset)

    override fun inputProcessor() = ColorPaletteInputProcessor()

    companion object {
        const val ColorPaletteBridge = "ColorPaletteBridge"
    }

}