package river.exertion.kcop.simulation.view.ctrl

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick
import river.exertion.kcop.simulation.view.ViewType
import river.exertion.kcop.system.messaging.MessageChannel
import river.exertion.kcop.system.messaging.Switchboard
import river.exertion.kcop.system.messaging.messages.DisplayViewMenuMessage
import river.exertion.kcop.system.messaging.messages.KcopMessage

class MenuViewCtrl(screenWidth: Float = 50f, screenHeight: Float = 50f) : Telegraph, ViewCtrl(ViewType.MENU, screenWidth, screenHeight) {

    init {
        MessageChannel.MENU_VIEW_BRIDGE.enableReceive(this)
        MessageChannel.DISPLAY_MODE_BRIDGE.enableReceive(this)

        MessageChannel.SDC_BRIDGE.enableReceive(this)
        MessageChannel.KCOP_SKIN_BRIDGE.enableReceive(this)
    }

    var isChecked : MutableMap<Int, Boolean> = mutableMapOf()

    fun buttonLayout() : Table {
        //idx 0 is large button, idx 1 - 5 are smaller bordering buttons
        val buttonList = mutableListOf<Button>()

        (0..5).forEach { idx ->

            val innerButton = Button(skin()).apply {kcopSkin.addOnEnter(this); kcopSkin.addOnClick(this)}

            //override from ctrl
            innerButton.isChecked = this@MenuViewCtrl.isChecked[idx] == true

            innerButton.onClick {
                this@MenuViewCtrl.isChecked[idx] = !(this@MenuViewCtrl.isChecked[idx] ?: false)
                when (idx) {
                    0 -> {
                        if (this@MenuViewCtrl.isChecked[idx] == true)
                            Switchboard.openMenu()
                        else
                            Switchboard.closeMenu()
                    }
                    1 -> MessageChannel.DISPLAY_MODE_BRIDGE.send(null, this@MenuViewCtrl.isChecked[idx]!!)
                    3 -> MessageChannel.KCOP_BRIDGE.send(null, KcopMessage(KcopMessage.KcopMessageType.FullScreen))
                    4 -> {
                        if (this@MenuViewCtrl.isChecked[idx]!!) {
                            MessageChannel.KCOP_BRIDGE.send(null, KcopMessage(KcopMessage.KcopMessageType.ShowColorPalette))
                        } else {
                            MessageChannel.KCOP_BRIDGE.send(null, KcopMessage(KcopMessage.KcopMessageType.HideColorPalette))
                        }
                    }
                    else -> MessageChannel.DISPLAY_VIEW_MENU_BRIDGE.send(null, DisplayViewMenuMessage(null, idx, this@MenuViewCtrl.isChecked[idx]!!))
                }
            }

            buttonList.add(innerButton)
        }

        val buttonSubLayout1 = Table()
        buttonSubLayout1.add(buttonList[1]).size(ViewType.seventhWidth(screenWidth), ViewType.seventhHeight(screenHeight))
        buttonSubLayout1.add(buttonList[2]).size(ViewType.seventhWidth(screenWidth), ViewType.seventhHeight(screenHeight))
        buttonSubLayout1.row()
        buttonSubLayout1.add(buttonList[0]).align(Align.center).colspan(2).size(ViewType.sixthWidth(screenWidth) - 3, ViewType.sixthHeight(screenHeight) - 3)

        val buttonSubLayout2 = Table()
        buttonSubLayout2.add(buttonList[3]).align(Align.center).size(ViewType.seventhWidth(screenWidth) - 5, ViewType.seventhHeight(screenHeight) - 5)
        buttonSubLayout2.row()
        buttonSubLayout2.add(buttonList[4]).size(ViewType.seventhWidth(screenWidth), ViewType.seventhHeight(screenHeight)).padTop(1f)
        buttonSubLayout2.row()
        buttonSubLayout2.add(buttonList[5]).size(ViewType.seventhWidth(screenWidth), ViewType.seventhHeight(screenHeight))

        val buttonLayout = Table()
        buttonLayout.add(buttonSubLayout1).padTop(2f).padLeft(2f).padBottom(2f)
        buttonLayout.add(buttonSubLayout2).padTop(2f).padRight(1f).padBottom(2f)

        buttonLayout.validate()
        buttonLayout.layout()

        return buttonLayout
    }

    override fun buildCtrl() {
        this.add(Stack().apply {
            this.add(backgroundColorImg())
            this.add(buttonLayout())
        } ).size(this.tableWidth(), this.tableHeight())

        this.clip()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg != null) {
            when {
                (MessageChannel.SDC_BRIDGE.isType(msg.message) ) -> {
                    super.sdcHandler = MessageChannel.SDC_BRIDGE.receiveMessage(msg.extraInfo)
                    return true
                }
                (MessageChannel.KCOP_SKIN_BRIDGE.isType(msg.message) ) -> {
                    super.kcopSkin = MessageChannel.KCOP_SKIN_BRIDGE.receiveMessage(msg.extraInfo)
                    return true
                }
                (MessageChannel.DISPLAY_MODE_BRIDGE.isType(msg.message) ) -> {
                    this.currentLayoutMode = MessageChannel.DISPLAY_MODE_BRIDGE.receiveMessage(msg.extraInfo)
                    build()
                    return true
                }
                (MessageChannel.MENU_VIEW_BRIDGE.isType(msg.message) ) -> {
                    val displayViewMenuMessage : DisplayViewMenuMessage = MessageChannel.MENU_VIEW_BRIDGE.receiveMessage(msg.extraInfo)

                    if (displayViewMenuMessage.menuButtonIdx != null) {
                        this@MenuViewCtrl.isChecked[displayViewMenuMessage.menuButtonIdx] = displayViewMenuMessage.isChecked
                    }

                    build()
                    return true
                }
            }
        }
        return false
    }
}