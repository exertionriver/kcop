package river.exertion.kcop.sim.narrative.menu

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.actors.onChange
import ktx.collections.toGdxArray
import river.exertion.kcop.messaging.MessageChannelHandler
import river.exertion.kcop.sim.narrative.NarrativePackage.Companion.NarrativeMenuDataBridge
import river.exertion.kcop.sim.narrative.messaging.NarrativeMenuDataMessage
import river.exertion.kcop.view.ColorPalette
import river.exertion.kcop.view.KcopSkin
import river.exertion.kcop.view.SdcHandler
import river.exertion.kcop.view.ViewPackage.KcopSkinBridge
import river.exertion.kcop.view.ViewPackage.MenuNavBridge
import river.exertion.kcop.view.ViewPackage.MenuViewBridge
import river.exertion.kcop.view.ViewPackage.SDCBridge
import river.exertion.kcop.view.menu.DisplayViewMenu
import river.exertion.kcop.view.menu.MainMenu
import river.exertion.kcop.view.messaging.MenuNavMessage
import river.exertion.kcop.view.messaging.MenuViewMessage
import river.exertion.kcop.view.messaging.menuParams.ActionParam
import river.exertion.kcop.view.messaging.menuParams.MenuNavParams

class NarrativeMenu(override var screenWidth: Float, override var screenHeight: Float) : Telegraph, DisplayViewMenu {

    init {
        MessageChannelHandler.enableReceive(NarrativeMenuDataBridge, this)
        MessageChannelHandler.enableReceive(MenuNavBridge, this)

        MessageChannelHandler.enableReceive(SDCBridge, this)
        MessageChannelHandler.enableReceive(KcopSkinBridge, this)
    }

    override lateinit var sdcHandler : SdcHandler
    override lateinit var kcopSkin: KcopSkin

    override val backgroundColor = ColorPalette.of("green")

    var narrativeAssetTitles: List<String>? = null
    var selectedNarrativeAssetTitle : String? = null

    override fun menuPane() : Table {

        val listCtrl = com.badlogic.gdx.scenes.scene2d.ui.List<String>(skin())
//                ListStyle().apply {
//            this.font = bitmapFont
//            this.selection = TextureRegionDrawable(TextureRegion(Texture("images/kobold64.png")))
//        })

        if (narrativeAssetTitles != null) {
            selectedNarrativeAssetTitle = narrativeAssetTitles!![0]

            listCtrl.onChange {
                navs.forEach {
                    selectedNarrativeAssetTitle = narrativeAssetTitles?.get(this.selectedIndex)
                }
            }

            listCtrl.setItems(narrativeAssetTitles!!.toGdxArray())
        } else {
            listCtrl.setItems(listOf("no profiles found").toGdxArray() )
        }

        return Table().apply {
            this.add(listCtrl).growY().top().left()
//            this.add(Table())
//            this.debug()
            this.top()
            this.left()
        }
    }

    override val breadcrumbEntries = mapOf(
        MainMenu.tag to MainMenu.label
    )

    override val navs = mutableListOf(
        ActionParam("Load >", {
//            MessageChannelEnum.AMH_LOAD_BRIDGE.send(null, AMHLoadMessage(AMHLoadMessage.AMHLoadMessageType.SetSelectedNarrativeFromAsset, selectedNarrativeAssetTitle))
            MessageChannelHandler.send(MenuNavBridge, MenuNavMessage(MenuNavParams(selectedNarrativeAssetTitle)))
            MessageChannelHandler.send(MenuViewBridge, MenuViewMessage(LoadNarrativeMenu.tag) )
        }),
        ActionParam("Restart >", {
//            MessageChannelEnum.AMH_LOAD_BRIDGE.send(null, AMHLoadMessage(AMHLoadMessage.AMHLoadMessageType.SetSelectedNarrativeFromAsset, selectedNarrativeAssetTitle))
            MessageChannelHandler.send(MenuNavBridge, MenuNavMessage(MenuNavParams(selectedNarrativeAssetTitle)))
 //           MessageChannelHandler.send(MenuViewBridge, MenuViewMessage(RestartProgressMenu.tag) )
        }),
    )

    override val actions = mutableListOf<ActionParam>()

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg != null) {
            when {
                (MessageChannelHandler.isType(SDCBridge, msg.message) ) -> {
                    sdcHandler = MessageChannelHandler.receiveMessage(SDCBridge, msg.extraInfo)
                    return true
                }
                (MessageChannelHandler.isType(KcopSkinBridge, msg.message) ) -> {
                    kcopSkin = MessageChannelHandler.receiveMessage(KcopSkinBridge, msg.extraInfo)
                    return true
                }
                (MessageChannelHandler.isType(NarrativeMenuDataBridge, msg.message)) -> {
                    val menuDataMessage: NarrativeMenuDataMessage = MessageChannelHandler.receiveMessage(NarrativeMenuDataBridge, msg.extraInfo)

                    if ( menuDataMessage.narrativeMenuDataParams != null ) {
                            if (menuDataMessage.narrativeMenuDataParams!!.narrativeAssetTitles != null) {
                                narrativeAssetTitles = menuDataMessage.narrativeMenuDataParams!!.narrativeAssetTitles
                            }
                            if (menuDataMessage.narrativeMenuDataParams!!.selectedNarrativeAssetTitle != null) {
                                selectedNarrativeAssetTitle = menuDataMessage.narrativeMenuDataParams!!.selectedNarrativeAssetTitle
                            }
                    } else {
                        narrativeAssetTitles = null
                        selectedNarrativeAssetTitle = null
                    }
                    return true
                }
                (MessageChannelHandler.isType(MenuNavBridge, msg.message)) -> {
                    val menuNavMessage: MenuNavMessage = MessageChannelHandler.receiveMessage(MenuNavBridge, msg.extraInfo)

                    //between menus
                    selectedNarrativeAssetTitle = if (menuNavMessage.menuNavParams != null) {
                        menuNavMessage.menuNavParams!!.selectedAssetTitle
                    } else {
                        null
                    }

                    return true
                }
            }
        }
        return false
    }

    override fun tag() = tag
    override fun label() = label

    companion object {
        const val tag = "narrativeMenu"
        const val label = "Narrative"
    }
}