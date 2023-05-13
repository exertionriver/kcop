package river.exertion.kcop.view.layout

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick
import river.exertion.kcop.asset.view.ColorPalette
import river.exertion.kcop.view.KcopSkin
import river.exertion.kcop.view.SdcHandler

open class ViewBase(var viewType : ViewType) : Table() {

    fun viewRect() = viewType.viewRect(KcopSkin.screenWidth, KcopSkin.screenHeight)

    fun tableWidth() = viewRect().width
    fun tableHeight() = viewRect().height
    fun tablePosX() = viewRect().x
    fun tablePosY() = viewRect().y

    var currentLayoutMode = false

    var backgroundColor : ColorPalette = viewType.defaultColor()

    fun backgroundColorTexture() : TextureRegion {
        return if (currentLayoutMode) {
            SdcHandler.get("background_${viewType}", backgroundColor).textureRegion().apply {
                this.setRegion(0, 0, tableWidth().toInt() - 1, tableHeight().toInt() - 1)
            }
        } else {
            SdcHandler.get("background_${viewType}", KcopSkin.BackgroundColor).textureRegion().apply {
                this.setRegion(0, 0, tableWidth().toInt() - 1, tableHeight().toInt() - 1)
            }
        }
    }

    fun backgroundColorImg() : Image = Image(backgroundColorTexture()).apply { this.setFillParent(true) }

    private fun clearTable() {
        this.clearChildren()
        this.clearListeners()

        this.setSize(tableWidth(), tableHeight())
        this.setPosition(tablePosX(), tablePosY())
    }

    fun build() {
        clearTable()
        buildCtrl()
    }

    open fun buildCtrl() {
        val stack = Stack()

        val viewLabel = Label(viewType.name, skin)
                //Label.LabelStyle(fontPackage.font(FontSize.TEXT), backgroundColor.label().color()))
        viewLabel.setAlignment(Align.center)

        stack.onClick {
            println("layout View:${viewType.name}")
            println("x:${Gdx.input.getX()}, y:${Gdx.input.getY()}")
        }

        stack.add(backgroundColorImg())
        stack.add(viewLabel)

        this.add(stack).size(this.tableWidth(), this.tableHeight())
        this.clip()
    }
}