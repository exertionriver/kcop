package river.exertion.kcop.simulation.view

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Table
import river.exertion.kcop.simulation.view.ctrl.*
import river.exertion.kcop.simulation.view.displayViewMenus.LoadProfileMenu
import river.exertion.kcop.simulation.view.displayViewMenus.ProfileMenuParams
import river.exertion.kcop.simulation.view.displayViewMenus.SaveProfileMenu
import river.exertion.kcop.system.messaging.MessageChannel
import river.exertion.kcop.system.ecs.component.NarrativeComponent
import river.exertion.kcop.system.messaging.*
import river.exertion.kcop.system.messaging.messages.*
import river.exertion.kcop.system.profile.Status
import river.exertion.kcop.system.view.*

class ViewLayout(var width : Float, var height : Float) : Telegraph {

    var displayViewCtrl = DisplayViewCtrl(width, height)
    var textViewCtrl = TextViewCtrl(width, height)
    var logViewCtrl = LogViewCtrl(width, height)
    var statusViewCtrl = StatusViewCtrl(width, height)
    var menuViewCtrl = MenuViewCtrl(width, height)
    var inputsViewCtrl = InputViewCtrl(width, height)
    var aiViewCtrl = AiViewCtrl(width, height)
    var pauseViewCtrl = PauseViewCtrl(width, height)

    var currentNarrativeId : String? = null
    var currentInstImmersionTimerId : String? = null
    var currentCumlImmersionTimerId : String? = null
    var currentInstBlockTimerId : String? = null
    var currentCumlBlockTimerId : String? = null

    init {
        MessageChannel.LAYOUT_BRIDGE.enableReceive(this)
        MessageChannel.DISPLAY_VIEW_TEXTURE_BRIDGE.enableReceive(this)
        MessageChannel.DISPLAY_VIEW_AUDIO_BRIDGE.enableReceive(this)
        MessageChannel.DISPLAY_VIEW_MENU_BRIDGE.enableReceive(this)
        MessageChannel.TEXT_VIEW_BRIDGE.enableReceive(this)
        MessageChannel.LOG_VIEW_BRIDGE.enableReceive(this)
        MessageChannel.INPUT_VIEW_BRIDGE.enableReceive(this)
        MessageChannel.STATUS_VIEW_BRIDGE.enableReceive(this)
        MessageChannel.NARRATIVE_PROMPT_BRIDGE_PAUSE_GATE.enableReceive(this)
        MessageChannel.NARRATIVE_COMPONENT_BRIDGE.enableReceive(this)
    }

    private fun createViewCtrl(layoutViewCtrl : ViewCtrl, batch : Batch, bitmapFont : BitmapFont) : Table {
        layoutViewCtrl.initCreate(bitmapFont, batch)
        return layoutViewCtrl
    }

    fun createDisplayViewCtrl(batch : Batch, bitmapFont : BitmapFont) : DisplayViewCtrl {

        displayViewCtrl.initCreate(bitmapFont, batch)

        return displayViewCtrl
    }

    fun createTextViewCtrl(batch : Batch, bitmapFont : BitmapFont, vScrollKnobImage : Texture) : TextViewCtrl {
        textViewCtrl.vScrollKnobTexture = vScrollKnobImage

        textViewCtrl.initCreate(bitmapFont, batch)

        return textViewCtrl
    }
    fun createLogViewCtrl(batch : Batch, bitmapFont : BitmapFont, vScrollImage : Texture, vScrollKnobImage : Texture) : LogViewCtrl {
        logViewCtrl.vScrollTexture = vScrollImage
        logViewCtrl.vScrollKnobTexture = vScrollKnobImage

        logViewCtrl.initCreate(bitmapFont, batch)

        return logViewCtrl
    }
    fun createStatusViewCtrl(batch : Batch, bitmapFont : BitmapFont, vScrollKnobImage : Texture) : StatusViewCtrl {
        statusViewCtrl.vScrollKnobTexture = vScrollKnobImage

        statusViewCtrl.initCreate(bitmapFont, batch)

        return statusViewCtrl
    }

    fun createMenuViewCtrl(batch : Batch, bitmapFont : BitmapFont, upImage : Texture, downImage : Texture, checkedImage : Texture) : MenuViewCtrl {

        (0..5).forEach {
            menuViewCtrl.menuUpImage[it] = upImage
            menuViewCtrl.menuDownImage[it] = downImage
            menuViewCtrl.menuCheckedImage[it] = checkedImage
        }

        menuViewCtrl.initCreate(bitmapFont, batch)

        return menuViewCtrl
    }

    fun createInputsViewCtrl(batch : Batch, bitmapFont : BitmapFont, clickImage : Texture, keyPressImage : Texture, keyUpImage : Texture) : InputViewCtrl {
        inputsViewCtrl.clickImage = clickImage
        inputsViewCtrl.keyPressImage = keyPressImage
        inputsViewCtrl.keyUpImage = keyUpImage

        inputsViewCtrl.initCreate(bitmapFont, batch)

        return inputsViewCtrl
    }
    fun createAiViewCtrl(batch : Batch, bitmapFont : BitmapFont, upImage : Texture, downImage : Texture, checkedImage: Texture) : AiViewCtrl {
        aiViewCtrl.aiUpImage = upImage
        aiViewCtrl.aiDownImage = downImage
        aiViewCtrl.aiCheckedImage = checkedImage

        aiViewCtrl.initCreate(bitmapFont, batch)

        return aiViewCtrl
    }

    fun createPauseViewCtrl(batch : Batch, bitmapFont : BitmapFont, upImage : Texture, downImage : Texture, checkedImage : Texture) : PauseViewCtrl {
        pauseViewCtrl.pauseUpImage = upImage
        pauseViewCtrl.pauseDownImage = downImage
        pauseViewCtrl.pauseCheckedImage = checkedImage

        pauseViewCtrl.initCreate(bitmapFont, batch)

        return pauseViewCtrl
    }

    fun resetNarrative(narrativeComponent: NarrativeComponent) {
        currentInstImmersionTimerId = narrativeComponent.instNarrativeTimerId()
        currentCumlImmersionTimerId = narrativeComponent.cumlNarrativeTimerId()
        currentInstBlockTimerId = narrativeComponent.instBlockTimerId()
        currentCumlBlockTimerId = narrativeComponent.cumlBlockTimerId()
        currentNarrativeId = narrativeComponent.narrativeId()

        displayViewCtrl.setLayoutIdxByTag(narrativeComponent.narrative!!.layoutTag)
        displayViewCtrl.currentLayoutMode = false
        displayViewCtrl.recreate()

        pauseViewCtrl.isChecked = false
        pauseViewCtrl.recreate()

        textViewCtrl.recreate()
    }

    @Suppress("NewApi")
    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg != null) {
            when {
                (MessageChannel.INPUT_VIEW_BRIDGE.isType(msg.message) ) -> {
                    val inputMessage : InputViewMessage = MessageChannel.INPUT_VIEW_BRIDGE.receiveMessage(msg.extraInfo)

                    if (inputMessage.event.isReleaseEvent()) {
                        inputsViewCtrl.releaseEvent()
                    } else {
                        if (inputMessage.event.isKeyEvent()) {
                            inputsViewCtrl.keyEvent(inputMessage.getKeyStr())
                        }
                        else if (inputMessage.event.isTouchEvent()) {
                            inputsViewCtrl.touchEvent(inputMessage.getScreenX(), inputMessage.getScreenY(), inputMessage.getButton())
                        }
                    }
                    if (inputsViewCtrl.isInitialized) inputsViewCtrl.recreate()
                }
                (MessageChannel.LOG_VIEW_BRIDGE.isType(msg.message) ) -> {
                    val logMessage : LogViewMessage = MessageChannel.LOG_VIEW_BRIDGE.receiveMessage(msg.extraInfo)

                    when (logMessage.messageType) {
                        LogViewMessageType.LogEntry -> {
                            logViewCtrl.addLog(logMessage.message)
                            if (logViewCtrl.isInitialized) logViewCtrl.recreate()
                        }
                        LogViewMessageType.InstImmersionTime -> {
                            if ( (logMessage.param != null) && (logMessage.param == this.currentInstImmersionTimerId) ) {
                                logViewCtrl.updateInstImmersionTime(logMessage.message)
                                if (logViewCtrl.isInitialized) logViewCtrl.rebuildTextTimeReadout()
                            }
                        }
                        LogViewMessageType.CumlImmersionTime -> {
                            if ( (logMessage.param != null) && (logMessage.param == this.currentCumlImmersionTimerId) ) {
                                logViewCtrl.updateCumlImmersionTime(logMessage.message)
                                if (logViewCtrl.isInitialized) logViewCtrl.rebuildTextTimeReadout()
                            }
                        }
                        LogViewMessageType.LocalTime -> {
                            logViewCtrl.updateLocalTime(logMessage.message)
                            if (logViewCtrl.isInitialized) logViewCtrl.rebuildTextTimeReadout()
                        }
                    }
                }
                (MessageChannel.NARRATIVE_COMPONENT_BRIDGE.isType(msg.message) ) -> {
                    val narrativeComponentMessage : NarrativeComponentMessage = MessageChannel.NARRATIVE_COMPONENT_BRIDGE.receiveMessage(msg.extraInfo)

                    resetNarrative(narrativeComponentMessage.narrativeComponent)
                }
                (MessageChannel.LAYOUT_BRIDGE.isType(msg.message) ) -> {
                    val viewMessage: ViewMessage = MessageChannel.LAYOUT_BRIDGE.receiveMessage(msg.extraInfo)

                    if ((viewMessage.targetView == ViewType.PAUSE) && (viewMessage.messageContent == ViewMessage.TogglePause)) {
                        MessageChannel.IMMERSION_TIME_BRIDGE.send(null, ImmersionTimerMessage(this.currentInstImmersionTimerId, null) )
                        MessageChannel.IMMERSION_TIME_BRIDGE.send(null, ImmersionTimerMessage(this.currentCumlImmersionTimerId, null) )
                        MessageChannel.IMMERSION_TIME_BRIDGE.send(null, ImmersionTimerMessage(this.currentInstBlockTimerId, null) )
                        MessageChannel.IMMERSION_TIME_BRIDGE.send(null, ImmersionTimerMessage(this.currentCumlBlockTimerId, null) )
                    }
                    if ( (viewMessage.targetView == ViewType.LOG) && (viewMessage.messageContent == ViewMessage.BlockInstTimer) ) {
                        this.currentInstBlockTimerId = viewMessage.param
                    }
                    if ( (viewMessage.targetView == ViewType.LOG) && (viewMessage.messageContent == ViewMessage.BlockCumlTimer) ) {
                        this.currentCumlBlockTimerId = viewMessage.param
                    }
                    if ( (viewMessage.targetView == ViewType.TEXT) && (viewMessage.messageContent == ViewMessage.BlockCumlTimer) ) {
                        this.currentCumlBlockTimerId = viewMessage.param
                    }
                }
                (MessageChannel.TEXT_VIEW_BRIDGE.isType(msg.message) ) -> {
                    val textViewMessage: TextViewMessage = MessageChannel.TEXT_VIEW_BRIDGE.receiveMessage(msg.extraInfo)

                    if ( textViewMessage.param == this.currentNarrativeId ) {
                        textViewCtrl.currentText = textViewMessage.narrativeText
                        textViewCtrl.currentPrompts = textViewMessage.prompts
                        if (textViewCtrl.isInitialized) textViewCtrl.recreate()

                        displayViewCtrl.currentText = textViewMessage.displayText
                        displayViewCtrl.currentFontSize = textViewMessage.displayFontSize
                        if (displayViewCtrl.isInitialized) displayViewCtrl.recreate()
                    }
                }
                (MessageChannel.NARRATIVE_PROMPT_BRIDGE_PAUSE_GATE.isType(msg.message) ) -> {
                    val promptMessage: ViewMessage = MessageChannel.NARRATIVE_PROMPT_BRIDGE_PAUSE_GATE.receiveMessage(msg.extraInfo)

                    if (!pauseViewCtrl.isChecked) {
                        MessageChannel.NARRATIVE_PROMPT_BRIDGE.send(null, promptMessage)
                    }
                }
                (MessageChannel.DISPLAY_VIEW_TEXTURE_BRIDGE.isType(msg.message) ) -> {
                    val displayViewTextureMessage: DisplayViewTextureMessage = MessageChannel.DISPLAY_VIEW_TEXTURE_BRIDGE.receiveMessage(msg.extraInfo)

                    when (displayViewTextureMessage.messageType) {
                        DisplayViewTextureMessageType.SHOW_IMAGE -> this.displayViewCtrl.showImage(displayViewTextureMessage.layoutPaneIdx, displayViewTextureMessage.texture)
                        DisplayViewTextureMessageType.FADE_IMAGE_IN -> this.displayViewCtrl.fadeImageIn(displayViewTextureMessage.layoutPaneIdx, displayViewTextureMessage.texture)
                        DisplayViewTextureMessageType.FADE_IMAGE_OUT -> this.displayViewCtrl.fadeImageOut(displayViewTextureMessage.layoutPaneIdx, displayViewTextureMessage.texture)
                        DisplayViewTextureMessageType.CROSSFADE_IMAGE -> this.displayViewCtrl.crossFadeImage(displayViewTextureMessage.layoutPaneIdx, displayViewTextureMessage.texture)
                        DisplayViewTextureMessageType.CLEAR_ALL -> this.displayViewCtrl.clearImages()
                    }

                    this.displayViewCtrl.recreate()
                }
                (MessageChannel.DISPLAY_VIEW_AUDIO_BRIDGE.isType(msg.message) ) -> {
                    val displayViewAudioMessage: DisplayViewAudioMessage = MessageChannel.DISPLAY_VIEW_AUDIO_BRIDGE.receiveMessage(msg.extraInfo)

                    when (displayViewAudioMessage.messageType) {
                        DisplayViewAudioMessageType.FADE_MUSIC_OUT -> displayViewCtrl.fadeMusicOut()
                        DisplayViewAudioMessageType.FADE_MUSIC_IN -> displayViewCtrl.fadeMusicIn(displayViewAudioMessage.music)
                        DisplayViewAudioMessageType.CROSS_FADE_MUSIC -> displayViewCtrl.crossFadeMusic(displayViewAudioMessage.music)
                        DisplayViewAudioMessageType.PLAY_MUSIC -> displayViewCtrl.playMusic(displayViewAudioMessage.music)
                        DisplayViewAudioMessageType.PLAY_SOUND -> displayViewCtrl.playSound(displayViewAudioMessage.music)
                        DisplayViewAudioMessageType.STOP_MUSIC -> displayViewCtrl.stopMusic()
                    }
                }
                (MessageChannel.DISPLAY_VIEW_MENU_BRIDGE.isType(msg.message) ) -> {
                    val displayViewMenuMessage: DisplayViewMenuMessage = MessageChannel.DISPLAY_VIEW_MENU_BRIDGE.receiveMessage(msg.extraInfo)

                    if ((displayViewMenuMessage.menuButtonIdx == null) && (displayViewMenuMessage.menuParams == null)) {
                        //close menu
                        displayViewCtrl.menuOpen = false
                        menuViewCtrl.isChecked[0] = false

                        menuViewCtrl.recreate()

                    }

                    if (displayViewMenuMessage.menuButtonIdx != null) {
                        if (displayViewMenuMessage.menuButtonIdx == 0) {
                            displayViewCtrl.menuOpen = (menuViewCtrl.isChecked[0] == true)
                            MessageChannel.LOG_VIEW_BRIDGE.send(null, LogViewMessage(LogViewMessageType.LogEntry, "Menu ${if (displayViewCtrl.menuOpen) "Opened" else "Closed"}" ))
                        }
                        if (displayViewMenuMessage.menuButtonIdx == 1) {
                            displayViewCtrl.currentLayoutMode = (menuViewCtrl.isChecked[1] == true)
                            MessageChannel.LOG_VIEW_BRIDGE.send(null, LogViewMessage(LogViewMessageType.LogEntry, "DisplayMode set to: ${if (displayViewCtrl.currentLayoutMode) "Background Box" else "Wireframe"}" ))
                        }
                        if (displayViewMenuMessage.menuButtonIdx == 2) {
                            displayViewCtrl.currentLayoutIdx = if (displayViewCtrl.currentLayoutIdx < displayViewCtrl.displayViewLayouts.size - 1) displayViewCtrl.currentLayoutIdx + 1 else 0
                            MessageChannel.LOG_VIEW_BRIDGE.send(null, LogViewMessage(LogViewMessageType.LogEntry, "Layout set to: ${displayViewCtrl.displayViewLayouts[displayViewCtrl.currentLayoutIdx].tag}" ))
                        }
                        if (displayViewMenuMessage.menuButtonIdx == 3) {
                            displayViewCtrl.currentMenuIdx = if (displayViewCtrl.currentMenuIdx < displayViewCtrl.displayViewMenus.size - 1) displayViewCtrl.currentMenuIdx + 1 else 0
                            MessageChannel.LOG_VIEW_BRIDGE.send(null, LogViewMessage(LogViewMessageType.LogEntry, "Menu set to: ${displayViewCtrl.displayViewMenus[displayViewCtrl.currentMenuIdx].tag()}" ))
                        }
                    }

                    if (displayViewMenuMessage.menuParams != null) {
                        displayViewCtrl.currentMenuIdx = displayViewCtrl.displayViewMenus.indexOf(displayViewCtrl.displayViewMenus.firstOrNull { it.tag() == displayViewMenuMessage.menuParams!!.targetMenuTag } ?: 0)

                        if ( listOf(LoadProfileMenu.tag, SaveProfileMenu.tag).contains(displayViewMenuMessage.menuParams!!.targetMenuTag) ) {
                            (displayViewCtrl.displayViewMenus[displayViewCtrl.currentMenuIdx] as LoadProfileMenu).profileAsset = (displayViewMenuMessage.menuParams as ProfileMenuParams).profile
                        }
                    }

                    this.displayViewCtrl.recreate()
                }
                (MessageChannel.STATUS_VIEW_BRIDGE.isType(msg.message) ) -> {
                    val statusViewMessage: StatusViewMessage = MessageChannel.STATUS_VIEW_BRIDGE.receiveMessage(msg.extraInfo)

                    when (statusViewMessage.messageType) {
                        StatusViewMessageType.ADD_STATUS -> {
                            if (statusViewMessage.statusKey != null) {
                                this.statusViewCtrl.statuses.add(Status(statusViewMessage.statusKey, (statusViewMessage.statusValue ?: 0f)))
                            }
                        }
                        StatusViewMessageType.CLEAR_STATUSES -> {
                            this.statusViewCtrl.statuses.clear()
                        }
                        StatusViewMessageType.REMOVE_STATUS -> {
                            this.statusViewCtrl.statuses.removeIf { it.key == statusViewMessage.statusKey }
                        }
                        StatusViewMessageType.UPDATE_STATUS -> {
                            this.statusViewCtrl.statuses.firstOrNull { it.key == statusViewMessage.statusKey }?.value = statusViewMessage.statusValue ?: 0f
                        }
                    }
                    this.statusViewCtrl.recreate()
                }
            }
        }
        return true
    }

    fun dispose() {
        displayViewCtrl.dispose()
        textViewCtrl.dispose()
        logViewCtrl.dispose()
        statusViewCtrl.dispose()
        menuViewCtrl.dispose()
        inputsViewCtrl.dispose()
        aiViewCtrl.dispose()
        pauseViewCtrl.dispose()
    }
}