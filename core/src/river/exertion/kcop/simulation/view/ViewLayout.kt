package river.exertion.kcop.simulation.view

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Timer
import river.exertion.kcop.system.MessageChannel
import river.exertion.kcop.system.component.NarrativeComponent
import river.exertion.kcop.system.view.*

class ViewLayout(var width : Float, var height : Float) : Telegraph {

    var displayViewCtrl = DisplayViewCtrl(width, height)
    var textViewCtrl = TextViewCtrl(width, height)
    var logViewCtrl = LogViewCtrl(width, height)
    var statusViewCtrl = StatusViewCtrl(width, height)
    var menuViewCtrl = ViewCtrl(ViewType.MENU, width, height)
    var inputsViewCtrl = InputViewCtrl(width, height)
    var aiViewCtrl = ViewCtrl(ViewType.AI, width, height)
    var pauseViewCtrl = PauseViewCtrl(width, height)

    var currentNarrativeId : String? = null
    var currentInstImmersionTimerId : String? = null
    var currentCumlImmersionTimerId : String? = null
    var currentInstBlockTimerId : String? = null
    var currentCumlBlockTimerId : String? = null
    var currentMusic : Music? = null
    var currentSound : Music? = null

    init {
        MessageChannel.LAYOUT_BRIDGE.enableReceive(this)
        MessageChannel.DISPLAY_VIEW_TEXTURE_BRIDGE.enableReceive(this)
        MessageChannel.DISPLAY_VIEW_AUDIO_BRIDGE.enableReceive(this)
        MessageChannel.TEXT_VIEW_BRIDGE.enableReceive(this)
        MessageChannel.LOG_VIEW_BRIDGE.enableReceive(this)
        MessageChannel.INPUT_VIEW_BRIDGE.enableReceive(this)
        MessageChannel.STATUS_VIEW_BRIDGE.enableReceive(this)
        MessageChannel.NARRATIVE_PROMPT_BRIDGE_PAUSE_GATE.enableReceive(this)
    }

    private fun createViewCtrl(layoutViewCtrl : ViewCtrl, batch : Batch, bitmapFont : BitmapFont) : Table {
        layoutViewCtrl.initCreate(bitmapFont, batch)
        return layoutViewCtrl
    }

    fun createDisplayViewCtrl(batch : Batch, bitmapFont : BitmapFont, largeImage : Texture? = null, mediumImage : Texture? = null, smallImage : Texture? = null, tinyImage : Texture? = null) : DisplayViewCtrl {
        displayViewCtrl.displayViewLayouts[0].paneTextures[2] = largeImage
        displayViewCtrl.displayViewLayouts[0].paneTextures[4] = mediumImage
        displayViewCtrl.displayViewLayouts[0].paneTextures[12] = smallImage
        displayViewCtrl.displayViewLayouts[0].paneTextures[16] = tinyImage

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

    fun createPromptsViewCtrl(batch : Batch, bitmapFont : BitmapFont) = createViewCtrl(menuViewCtrl, batch, bitmapFont)
    fun createInputsViewCtrl(batch : Batch, bitmapFont : BitmapFont, clickImage : Texture, keyPressImage : Texture, keyUpImage : Texture) : InputViewCtrl {
        inputsViewCtrl.clickImage = clickImage
        inputsViewCtrl.keyPressImage = keyPressImage
        inputsViewCtrl.keyUpImage = keyUpImage

        inputsViewCtrl.initCreate(bitmapFont, batch)

        return inputsViewCtrl
    }
    fun createAiViewCtrl(batch : Batch, bitmapFont : BitmapFont) = createViewCtrl(aiViewCtrl, batch, bitmapFont)
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

        pauseViewCtrl.isChecked = false
        pauseViewCtrl.recreate()
    }

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
                }
                (MessageChannel.TEXT_VIEW_BRIDGE.isType(msg.message) ) -> {
                    val textViewMessage: TextViewMessage = MessageChannel.TEXT_VIEW_BRIDGE.receiveMessage(msg.extraInfo)

                    if ( textViewMessage.param == this.currentNarrativeId ) {
                        textViewCtrl.currentText = textViewMessage.narrativeText
                        textViewCtrl.currentPrompts = textViewMessage.prompts
                        if (textViewCtrl.isInitialized) textViewCtrl.recreate()
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
                        DisplayViewTextureMessageType.IMAGE_LARGE -> this.displayViewCtrl.displayViewLayouts[0].paneTextures[2] = displayViewTextureMessage.texture
                        DisplayViewTextureMessageType.FADE_IMAGE_LARGE_IN -> {
                            if (this.displayViewCtrl.displayViewLayouts[0].paneTextures[2] != displayViewTextureMessage.texture && !this.displayViewCtrl.imageIsFading) {
                                this.displayViewCtrl.displayViewLayouts[0].paneTextures[2] = displayViewTextureMessage.texture
                                this.displayViewCtrl.imageIsFading = true
                                this.displayViewCtrl.displayViewLayouts[0].paneTextureMaskAlpha[2] = 1f
                                Timer.schedule(object : Timer.Task() {
                                    override fun run() {
                                        if (this@ViewLayout.displayViewCtrl.displayViewLayouts[0].paneTextureMaskAlpha[2]!! >= .1)
                                            this@ViewLayout.displayViewCtrl.displayViewLayouts[0].paneTextureMaskAlpha[2] =
                                            this@ViewLayout.displayViewCtrl.displayViewLayouts[0].paneTextureMaskAlpha[2]!! - .1f
                                        else {
                                            this@ViewLayout.displayViewCtrl.imageIsFading = false
                                            this.cancel()
                                        }
                                        this@ViewLayout.displayViewCtrl.recreate()
                                    }
                                }, 0f, .05f)
                            }
                        }
                        DisplayViewTextureMessageType.FADE_IMAGE_LARGE_OUT -> {
                            if (this.displayViewCtrl.displayViewLayouts[0].paneTextures[2] != displayViewTextureMessage.texture && !this.displayViewCtrl.imageIsFading) {
                                this.displayViewCtrl.imageIsFading = true
                                Timer.schedule(object : Timer.Task() {
                                    override fun run() {
                                        if (this@ViewLayout.displayViewCtrl.displayViewLayouts[0].paneTextureMaskAlpha[2]!! <= .9)
                                            this@ViewLayout.displayViewCtrl.displayViewLayouts[0].paneTextureMaskAlpha[2] =
                                            this@ViewLayout.displayViewCtrl.displayViewLayouts[0].paneTextureMaskAlpha[2]!! + .1f
                                        else {
                                            this@ViewLayout.displayViewCtrl.displayViewLayouts[0].paneTextures[2] = displayViewTextureMessage.texture
                                            this@ViewLayout.displayViewCtrl.imageIsFading = false
                                            this.cancel()
                                        }
                                        this@ViewLayout.displayViewCtrl.recreate()
                                    }
                                }, 0f, .05f)
                            }
                        }
                        DisplayViewTextureMessageType.CROSSFADE_IMAGE_LARGE -> this.displayViewCtrl.displayViewLayouts[0].paneTextures[2] = displayViewTextureMessage.texture
                        DisplayViewTextureMessageType.IMAGE_MEDIUM -> this.displayViewCtrl.displayViewLayouts[0].paneTextures[4] = displayViewTextureMessage.texture
                        DisplayViewTextureMessageType.IMAGE_SMALL -> this.displayViewCtrl.displayViewLayouts[0].paneTextures[12] = displayViewTextureMessage.texture
                        DisplayViewTextureMessageType.IMAGE_CLEAR -> {
                            displayViewCtrl.displayViewLayouts[0].paneTextures[2] = null
                            displayViewCtrl.displayViewLayouts[0].paneTextures[4] = null
                            displayViewCtrl.displayViewLayouts[0].paneTextures[12] = null
                            displayViewCtrl.displayViewLayouts[0].paneTextures[16] = null
                        }
                    }

                    this.displayViewCtrl.recreate()
                }
                (MessageChannel.DISPLAY_VIEW_AUDIO_BRIDGE.isType(msg.message) ) -> {
                    val displayViewAudioMessage: DisplayViewAudioMessage = MessageChannel.DISPLAY_VIEW_AUDIO_BRIDGE.receiveMessage(msg.extraInfo)

                    when (displayViewAudioMessage.messageType) {
                        DisplayViewAudioMessageType.FADE_MUSIC_OUT -> {
                            //https://stackoverflow.com/questions/35744181/libgdx-how-to-get-sfx-to-fade-out
                            Timer.schedule(object : Timer.Task() {
                                override fun run() {
                                    if ( (currentMusic?.volume ?: 0f) >= .1) currentMusic?.volume = currentMusic?.volume?.minus(.1f) ?: 0f
                                    else {
                                        currentMusic?.stop()
                                        currentMusic = null
                                        this.cancel()
                                    }
                                }
                            }, 0f, .3f)
                        }
                        DisplayViewAudioMessageType.FADE_MUSIC_IN -> {
                            if (displayViewAudioMessage.music != currentMusic) {
                                currentMusic?.stop()
                                currentMusic = displayViewAudioMessage.music
                                currentMusic?.isLooping = true
                                currentMusic?.volume = 0f
                                currentMusic?.play()

                                Timer.schedule(object : Timer.Task() {
                                    override fun run() {
                                        if ( (currentMusic?.volume ?: 1f) <= .9) currentMusic?.volume = currentMusic?.volume?.plus(.1f) ?: 1f
                                        else {
                                            this.cancel()
                                        }
                                    }
                                }, 0f, .3f)
                            }
                        }
                        DisplayViewAudioMessageType.CROSS_FADE_MUSIC -> {
                            if (displayViewAudioMessage.music != currentMusic) {
                                //fade current music out
                                val prevMusic = currentMusic
                                Timer.schedule(object : Timer.Task() {
                                    override fun run() {
                                        if ( (prevMusic?.volume ?: 0f) >= .1) prevMusic?.volume = prevMusic?.volume?.minus(.1f) ?: 0f
                                        else {
                                            prevMusic?.stop()
                                            this.cancel()
                                        }
                                    }
                                }, 0f, .3f)
                                currentMusic = displayViewAudioMessage.music
                                currentMusic?.isLooping = true
                                currentMusic?.volume = 0f
                                currentMusic?.play()

                                Timer.schedule(object : Timer.Task() {
                                    override fun run() {
                                        if ( (currentMusic?.volume ?: 1f) <= .9) currentMusic?.volume = currentMusic?.volume?.plus(.1f) ?: 1f
                                        else {
                                            this.cancel()
                                        }
                                    }
                                }, 0f, .3f)
                            }
                        }
                        DisplayViewAudioMessageType.PLAY_MUSIC -> {
                            if (displayViewAudioMessage.music != currentMusic) {
                                currentMusic?.stop()
                                currentMusic = displayViewAudioMessage.music
                                currentMusic?.isLooping = true
                                currentMusic?.volume = 1f
                                currentMusic?.play()
                            }
                        }
                        DisplayViewAudioMessageType.PLAY_SOUND -> {
                            if (displayViewAudioMessage.music != currentSound) {
                                currentSound = displayViewAudioMessage.music
                                currentSound?.isLooping = false
                                currentSound?.volume = 1f
                                currentSound?.play()
                            }
                        }
                        DisplayViewAudioMessageType.STOP_MUSIC -> currentMusic?.stop()
                    }
                }
                (MessageChannel.STATUS_VIEW_BRIDGE.isType(msg.message) ) -> {
                    val statusViewMessage: StatusViewMessage = MessageChannel.STATUS_VIEW_BRIDGE.receiveMessage(msg.extraInfo)

                    when (statusViewMessage.messageType) {
                        StatusViewMessageType.ADD_STATUS -> {
                            this.statusViewCtrl.statuses[statusViewMessage.statusKey] = statusViewMessage.statusValue ?: 0f
                        }
                        StatusViewMessageType.REMOVE_STATUS -> {
                            this.statusViewCtrl.statuses.remove(statusViewMessage.statusKey)
                        }
                        StatusViewMessageType.UPDATE_STATUS -> {
                            this.statusViewCtrl.statuses[statusViewMessage.statusKey] = statusViewMessage.statusValue ?: 0f
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