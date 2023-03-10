package river.exertion.kcop.simulation.view.ctrl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import river.exertion.kcop.simulation.view.ViewType
import river.exertion.kcop.system.view.ShapeDrawerConfig
import kotlin.reflect.jvm.javaMethod


class LogViewCtrl(screenWidth: Float = 50f, screenHeight: Float = 50f) : ViewCtrl(ViewType.LOG, screenWidth, screenHeight) {

    var currentLog : MutableList<String>? = null
    var textTimeSdc : ShapeDrawerConfig? = null

    var instImmersionTimeStr = "00:00:00"
    var cumlImmersionTimeStr = "00:00:00"
    var localTimeStr = "00:00:00"

    var vScrollTexture : Texture? = null
    var vScrollKnobTexture : Texture? = null

    var textTimePaneDimensions = Vector2(this.tableWidth() / 4, this.tableWidth() / 13)

    private lateinit var scrollPane : ScrollPane

    fun textTimebackgroundColorTexture(batch : Batch) : TextureRegion {
        if (this.textTimeSdc == null) this.textTimeSdc = ShapeDrawerConfig(batch, backgroundColor.triad().second.color())

        return textTimeSdc!!.textureRegion.apply {this.setRegion(0, 0, textTimePaneDimensions.x.toInt() - 1, textTimePaneDimensions.y.toInt() - 1) }
    }

    fun isLog() = !currentLog.isNullOrEmpty()

    fun addLog(logEntry : String) {
        if (currentLog == null) currentLog = mutableListOf()
        currentLog!!.add(logEntry)
    }

    fun updateInstImmersionTime(newImmersionTimeStr : String) {
        instImmersionTimeStr = newImmersionTimeStr
    }

    fun updateCumlImmersionTime(newImmersionTimeStr : String) {
        cumlImmersionTimeStr = newImmersionTimeStr
    }

    fun updateLocalTime(newLocalTimeStr : String) {
        localTimeStr = newLocalTimeStr
    }

    fun textTimeReadout(bitmapFont: BitmapFont, batch: Batch) : Table {

        val innerTable = Table().padLeft(ViewType.padWidth(width)).padRight(ViewType.padWidth(width)).padTop(ViewType.padHeight(height)).padBottom(ViewType.padHeight(height))

        innerTable.add(
            Stack().apply {
                this.add(Image(textTimebackgroundColorTexture(batch)))
                this.add(Table().apply {this.add(
                    Label(instImmersionTimeStr, Label.LabelStyle(bitmapFont, backgroundColor.label().color())).apply { this.setAlignment(Align.center)})
                    .padRight(ViewType.padWidth(width))
                })
            }).size(this.textTimePaneDimensions.x, this.textTimePaneDimensions.y)

        innerTable.add(
            Stack().apply {
                this.add(Image(textTimebackgroundColorTexture(batch)))
                this.add(Table().apply {this.add(
                    Label(cumlImmersionTimeStr, Label.LabelStyle(bitmapFont, backgroundColor.label().color())).apply { this.setAlignment(Align.center)})
                    .padLeft(this@LogViewCtrl.textTimePaneDimensions.y).padRight(this@LogViewCtrl.textTimePaneDimensions.y)
                    .size(this@LogViewCtrl.textTimePaneDimensions.x, this@LogViewCtrl.textTimePaneDimensions.y)
                })
            }).size(this.textTimePaneDimensions.x, this.textTimePaneDimensions.y)

        innerTable.add(
            Stack().apply {
                this.add(Image(textTimebackgroundColorTexture(batch)))
                this.add(Table().apply {this.add(
                    Label(localTimeStr, Label.LabelStyle(bitmapFont, backgroundColor.label().color())).apply { this.setAlignment(Align.center)})
                    .padRight(ViewType.padWidth(width))
                })
            }).size(this.textTimePaneDimensions.x, this.textTimePaneDimensions.y)

//        innerTable.debug()

        return innerTable
    }

    fun rebuildTextTimeReadout() {
        if (!isInitialized) throw Exception("${::rebuildTextTimeReadout.javaMethod?.name}: view needs to be initialized with " + ::initCreate.javaMethod?.name)

        this.clearChildren()

        this.add(Stack().apply {
            this.add(backgroundColorImg(this@LogViewCtrl.batch!!))
            this.add(Table().apply {
                this.add(textTimeReadout(this@LogViewCtrl.bitmapFont!!, this@LogViewCtrl.batch!!))
                this.row()
                this.add(scrollPane)
            })
        })
    }

    fun textScrollPane(bitmapFont: BitmapFont, batch : Batch) : ScrollPane {

        val innerTable = Table().padLeft(ViewType.padWidth(width)).padRight(ViewType.padWidth(width)).padTop(
            ViewType.padHeight(
                height
            )
        ).padBottom(
            ViewType.padHeight(height)
        )

        if (isLog()) {
            (currentLog!!.size - 1 downTo 0).forEach { revEntryIdx ->
                val logLabel = Label(currentLog!![revEntryIdx], Label.LabelStyle(bitmapFont, backgroundColor.label().color()))
                logLabel.wrap = true
                innerTable.add(logLabel).growX()
                innerTable.row()
            }
        }

        innerTable.top()
//        innerTable.debug()

        val scrollNine = NinePatch(TextureRegion(vScrollKnobTexture, 20, 20, 20, 20))
        val scrollPaneStyle = ScrollPane.ScrollPaneStyle(TextureRegionDrawable(backgroundColorTexture(batch)), null, null, null, NinePatchDrawable(scrollNine))

        val scrollPane = ScrollPane(innerTable, scrollPaneStyle).apply {
            // https://github.com/raeleus/skin-composer/wiki/ScrollPane
            this.fadeScrollBars = false
            this.setFlickScroll(false)
            this.validate()
            //https://gamedev.stackexchange.com/questions/96096/libgdx-scrollpane-wont-scroll-to-bottom-after-adding-children-to-contained-tab
            this.layout()
        }

        this.scrollPane = scrollPane

        return scrollPane
    }

    override fun build(bitmapFont: BitmapFont, batch: Batch) {

        this.add(Stack().apply {
            this.add(backgroundColorImg(batch))
            this.add(Table().apply {
                this.add(textTimeReadout(bitmapFont, batch))
                this.row()
                this.add(textScrollPane(bitmapFont, batch))
            })
        }).size(this.tableWidth(), this.tableHeight())
        this.clip()
    }
}