package river.exertion.kcop.simulation.view.displayViewMenus

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import river.exertion.kcop.assets.AssetManagerHandler
import river.exertion.kcop.simulation.view.displayViewMenus.params.ActionParam
import river.exertion.kcop.simulation.view.displayViewMenus.params.MenuNavParams
import river.exertion.kcop.system.colorPalette.ColorPalette
import river.exertion.kcop.system.messaging.MessageChannel
import river.exertion.kcop.system.messaging.Switchboard
import river.exertion.kcop.system.messaging.messages.AMHLoadMessage
import river.exertion.kcop.system.messaging.messages.AMHSaveMessage
import river.exertion.kcop.system.messaging.messages.MenuNavMessage
import river.exertion.kcop.system.view.ShapeDrawerConfig

class RestartProgressMenu(override var screenWidth: Float, override var screenHeight: Float) : DisplayViewMenu {

    override val sdcMap : MutableMap<Int, ShapeDrawerConfig?> = mutableMapOf()

    override val backgroundColor = ColorPalette.of("olive")

    var progressAssetsInfo: List<String>? = null

    override fun menuPane(bitmapFont: BitmapFont) = Table().apply {

        progressAssetsInfo!!.forEach { profileEntry ->
            this.add(Label(profileEntry, Label.LabelStyle(bitmapFont, backgroundColor.label().color())).apply {
                this.wrap = true
            }).colspan(2).growX()
            this.row()
        }

        this.top()
    }

    override val breadcrumbEntries = mapOf(
        ProfileMenu.tag to ProfileMenu.label,
        MainMenu.tag to MainMenu.label
    )

    override val navs = mutableListOf<ActionParam>()

    override val actions = mutableListOf(
        ActionParam("Restart", {
            Switchboard.closeMenu()
            MessageChannel.AMH_SAVE_BRIDGE.send(null, AMHSaveMessage(AMHSaveMessage.AMHSaveMessageType.RestartProgress))
        }, "Narrative Restarted!"),
        ActionParam("Cancel", { MessageChannel.INTRA_MENU_BRIDGE.send(null, MenuNavMessage(MenuNavParams(breadcrumbEntries.keys.toList()[0]) ))})
    )

    override fun tag() = tag
    override fun label() = label

    companion object {
        const val tag = "restartProgressMenu"
        const val label = "Restart Progress"
    }

}