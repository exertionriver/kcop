package river.exertion.kcop.simulation.view.ctrl

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Timer
import river.exertion.kcop.assets.FontSize
import river.exertion.kcop.simulation.view.ViewType
import river.exertion.kcop.simulation.view.displayViewLayouts.DVLBasicPictureNarrative
import river.exertion.kcop.simulation.view.displayViewLayouts.DVLGoldenRatio
import river.exertion.kcop.simulation.view.displayViewLayouts.DisplayViewLayout
import river.exertion.kcop.simulation.view.displayViewMenus.*

class DisplayViewCtrl(screenWidth: Float = 50f, screenHeight: Float = 50f) : ViewCtrl(ViewType.DISPLAY, screenWidth, screenHeight) {

    var displayViewLayouts : MutableList<DisplayViewLayout> = mutableListOf(
        DVLGoldenRatio(screenWidth, screenHeight),
        DVLBasicPictureNarrative(screenWidth, screenHeight),
    )

    var displayViewMenus : MutableList<DisplayViewMenu> = mutableListOf(
        MainMenu(screenWidth, screenHeight),
        ProfileMenu(screenWidth, screenHeight),
        LoadProfileMenu(screenWidth, screenHeight),
        SaveProfileMenu(screenWidth, screenHeight)
    )

    var menuOpen = false
    var currentMenuIdx = 0
    var currentLayoutIdx = 0

    var currentLayoutMode = false
    var currentText = ""
    var currentFontSize = FontSize.SMALL

    fun setLayoutIdxByTag(tag : String) {
        currentLayoutIdx = displayViewLayouts.indexOf(displayViewLayouts.firstOrNull { it.tag == tag } ?: displayViewLayouts[currentLayoutIdx])
    }

    var currentMusic : Music? = null
    var currentSound : Music? = null

    //clears if texture is null
    fun showImage(layoutPaneIdx : Int, texture : Texture?) {
        displayViewLayouts[currentLayoutIdx].paneTextures[layoutPaneIdx] = texture

        this.recreate()
    }

    fun fadeImageIn(layoutPaneIdx : Int, texture : Texture?) {
        if (displayViewLayouts[currentLayoutIdx].paneTextures[layoutPaneIdx] != texture && displayViewLayouts[currentLayoutIdx].paneImageFading[layoutPaneIdx] != true) {
            displayViewLayouts[currentLayoutIdx].paneTextures[layoutPaneIdx] = texture
            displayViewLayouts[currentLayoutIdx].paneImageFading[layoutPaneIdx] = true
            displayViewLayouts[currentLayoutIdx].paneTextureMaskAlpha[layoutPaneIdx] = 1f

            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    if (displayViewLayouts[currentLayoutIdx].paneTextureMaskAlpha[layoutPaneIdx]!! >= .1)
                        displayViewLayouts[currentLayoutIdx].paneTextureMaskAlpha[layoutPaneIdx] =
                            displayViewLayouts[currentLayoutIdx].paneTextureMaskAlpha[layoutPaneIdx]!! - .1f
                    else {
                        displayViewLayouts[currentLayoutIdx].paneImageFading[layoutPaneIdx] = false
                        this.cancel()
                    }
                    this@DisplayViewCtrl.recreate()
                }
            }, 0f, .05f)
        }
    }

    fun fadeImageOut(layoutPaneIdx : Int, texture : Texture?) {
        if (displayViewLayouts[currentLayoutIdx].paneTextures[layoutPaneIdx] != texture && displayViewLayouts[currentLayoutIdx].paneImageFading[layoutPaneIdx] != true) {
            displayViewLayouts[currentLayoutIdx].paneImageFading[layoutPaneIdx] = true
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    if (displayViewLayouts[currentLayoutIdx].paneTextureMaskAlpha[layoutPaneIdx]!! <= .9)
                        displayViewLayouts[currentLayoutIdx].paneTextureMaskAlpha[layoutPaneIdx] =
                            displayViewLayouts[currentLayoutIdx].paneTextureMaskAlpha[layoutPaneIdx]!! + .1f
                    else {
                        displayViewLayouts[currentLayoutIdx].paneTextures[layoutPaneIdx] = texture
                        displayViewLayouts[currentLayoutIdx].paneImageFading[layoutPaneIdx] = false
                        this.cancel()
                    }
                    this@DisplayViewCtrl.recreate()
                }
            }, 0f, .05f)
        }
    }

    //TODO: look into fade
    fun crossFadeImage(layoutPaneIdx : Int, texture : Texture?) {
            showImage(layoutPaneIdx, texture)
    }

    fun clearImages() {
        displayViewLayouts[currentLayoutIdx].paneTextures.clear()
        this.recreate()
    }

    fun fadeMusicOut() {
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

    fun fadeMusicIn(music : Music?) {
        if (music != currentMusic) {
            currentMusic?.stop()
            currentMusic = music
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

    fun crossFadeMusic(music : Music?) {
        if (music != currentMusic) {
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
            currentMusic = music
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

    fun playMusic(music : Music?) {
        if (music != currentMusic) {
            currentMusic?.stop()
            currentMusic = music
            currentMusic?.isLooping = true
            currentMusic?.volume = 1f
            currentMusic?.play()
        }
    }

    fun stopMusic() {
        currentMusic?.stop()
        currentMusic = null
    }

    fun playSound(sound : Music?) {
        if (sound != currentSound) {
            currentSound = sound
            currentSound?.isLooping = false
            currentSound?.volume = 1f
            currentSound?.play()
        }
    }

    override fun build(bitmapFont: BitmapFont, batch: Batch) {
        this.add(
            Stack().apply {
                this.add(displayViewLayouts[currentLayoutIdx].buildPaneTable(bitmapFont, batch, currentLayoutMode, currentText, currentFontSize))
                if (menuOpen) this.add(displayViewMenus[currentMenuIdx].menuLayout(batch, bitmapFont))
            }).size(this.tableWidth(), this.tableHeight())

        this.clip()
    }

    override fun dispose() {
        displayViewLayouts.forEach { it.dispose() }
        displayViewMenus.forEach { it.dispose() }
    }
}