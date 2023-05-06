package river.exertion.kcop.view.layout

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import river.exertion.kcop.ecs.system.SystemHandler
import river.exertion.kcop.messaging.MessageChannelHandler
import river.exertion.kcop.plugin.immersionTimer.ImmersionTimer
import river.exertion.kcop.view.FontSize
import river.exertion.kcop.view.KcopSkin
import river.exertion.kcop.view.ViewPackage.DisplayModeBridge
import river.exertion.kcop.view.ViewPackage.KcopSkinBridge
import river.exertion.kcop.view.ViewPackage.LogViewBridge
import river.exertion.kcop.view.ViewPackage.SDCBridge
import river.exertion.kcop.view.messaging.LogViewMessage
import river.exertion.kcop.view.system.TimeLogSystem

class LogView(screenWidth: Float = 50f, screenHeight: Float = 50f) : Telegraph, ViewBase(ViewType.LOG, screenWidth, screenHeight) {

    init {
        MessageChannelHandler.enableReceive(LogViewBridge, this)
        MessageChannelHandler.enableReceive(DisplayModeBridge, this)

        MessageChannelHandler.enableReceive(SDCBridge,this)
        MessageChannelHandler.enableReceive(KcopSkinBridge, this)

        SystemHandler.pooledEngine.addSystem(TimeLogSystem())
    }

    var currentLog : MutableList<String>? = null

    val initTimeStr = ImmersionTimer.CumlTimeZero

    var instImmersionTimeStr = initTimeStr
    var cumlImmersionTimeStr = initTimeStr
    var localTimeStr = initTimeStr

    var textTimePaneDimensions = Vector2(this.tableWidth() / 4, this.tableWidth() / 13)

    private var scrollPane : ScrollPane = textScrollPane()

    fun textTimeBackgroundColorTexture() : TextureRegion {
        return if (currentLayoutMode) {
            sdcHandler.get("textTime", backgroundColor.triad().second).textureRegion().apply {
                this.setRegion(0, 0, textTimePaneDimensions.x.toInt() - 1, textTimePaneDimensions.y.toInt() - 1)
            }
        } else {
            sdcHandler.get("textTime", KcopSkin.BackgroundColor).textureRegion().apply {
                this.setRegion(0, 0, textTimePaneDimensions.x.toInt() - 1, textTimePaneDimensions.y.toInt() - 1)
            }
        }
    }

    fun isLog() = !currentLog.isNullOrEmpty()

    fun addLog(logEntry : String) {
        if (currentLog != null) {
            currentLog!!.add(logEntry)
        } else {
            currentLog = mutableListOf(logEntry)
        }
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

    fun textTimeReadout() : Table {

        val innerTable = Table().padLeft(ViewType.padWidth(tableWidth())).padRight(ViewType.padWidth(tableWidth())).padTop(
            ViewType.padHeight(tableHeight())).padBottom(ViewType.padHeight(tableHeight()))

        innerTable.add(
            Stack().apply {
                this.add(Image(textTimeBackgroundColorTexture()))
                this.add(Table().apply {
                    this.add(Label(instImmersionTimeStr, kcopSkin.labelStyle(FontSize.TEXT, backgroundColor))
                    .apply {
                        this.setAlignment(Align.center)
                    })
                    .padRight(ViewType.padWidth(tableWidth()))
                })
            }).size(this.textTimePaneDimensions.x, this.textTimePaneDimensions.y)

        innerTable.add(
            Stack().apply {
                this.add(Image(textTimeBackgroundColorTexture()))
                this.add(Table().apply {
                    this.add(Label(cumlImmersionTimeStr, kcopSkin.labelStyle(FontSize.TEXT, backgroundColor))
                    .apply {
                        this.setAlignment(Align.center)
                    })
                    .padLeft(this@LogView.textTimePaneDimensions.y).padRight(this@LogView.textTimePaneDimensions.y)
                    .size(this@LogView.textTimePaneDimensions.x, this@LogView.textTimePaneDimensions.y)
                })
            }).size(this.textTimePaneDimensions.x, this.textTimePaneDimensions.y)

        innerTable.add(
            Stack().apply {
                this.add(Image(textTimeBackgroundColorTexture()))
                this.add(Table().apply {
                    this.add(Label(localTimeStr, kcopSkin.labelStyle(FontSize.TEXT, backgroundColor))
                    .apply {
                        this.setAlignment(Align.center)
                    })
                    .padRight(ViewType.padWidth(tableWidth()))
                })
            }).size(this.textTimePaneDimensions.x, this.textTimePaneDimensions.y)

       // innerTable.debug()

        return innerTable
    }

    fun rebuildTextTimeReadout() {
        this.clearChildren()

        this.add(Stack().apply {
            this.add(Table().apply {
                this.add(backgroundColorImg()).grow()
            })
            this.add(Table().apply {
                this.add(textTimeReadout())
                this.row()
                this.add(scrollPane).grow()
            })
        }).size(this.tableWidth(), this.tableHeight())
        this.clip()
    }

    fun textScrollPane() : ScrollPane {

        val innerTable = Table()
            .padLeft(ViewType.padWidth(tableWidth()))
            .padRight(ViewType.padWidth(tableWidth()))
            .padTop(ViewType.padHeight(tableHeight()))
            .padBottom(
                ViewType.padHeight(tableHeight())
        )

        if (isLog()) {
            (currentLog!!.size - 1 downTo 0).forEach { revEntryIdx ->
                val logLabel = Label(currentLog!![revEntryIdx], kcopSkin.labelStyle(FontSize.TEXT, backgroundColor))
                logLabel.wrap = true
                innerTable.add(logLabel).growX()
                innerTable.row()
            }
        }

        innerTable.top()
//        innerTable.debug()

        val scrollPane = ScrollPane(innerTable, skin()).apply {
            // https://github.com/raeleus/skin-composer/wiki/ScrollPane
            this.fadeScrollBars = false
            this.setFlickScroll(false)
            this.validate()
            //https://gamedev.stackexchange.com/questions/96096/libgdx-scrollpane-wont-scroll-to-bottom-after-adding-children-to-contained-tab
            this.layout()
//            this.debug()
        }

        this.scrollPane = scrollPane

        return scrollPane
    }

    override fun buildCtrl() {

        this.add(Stack().apply {
            this.add(Table().apply {
                this.add(backgroundColorImg()).grow()
            })
            this.add(Table().apply {
//                this.debug()
                this.add(textTimeReadout())
                this.row()
                this.add(textScrollPane()).grow()
            })
 //           this.debug()
        }).size(this.tableWidth(), this.tableHeight())
        this.clip()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg != null) {
            when {
                (MessageChannelHandler.isType(SDCBridge, msg.message) ) -> {
                    super.sdcHandler = MessageChannelHandler.receiveMessage(SDCBridge, msg.extraInfo)
                    return true
                }
                (MessageChannelHandler.isType(KcopSkinBridge, msg.message) ) -> {
                    super.kcopSkin = MessageChannelHandler.receiveMessage(KcopSkinBridge, msg.extraInfo)
                    return true
                }
                (MessageChannelHandler.isType(DisplayModeBridge, msg.message) ) -> {
                    this.currentLayoutMode = MessageChannelHandler.receiveMessage(DisplayModeBridge, msg.extraInfo)
                    build()
                    return true
                }
                (MessageChannelHandler.isType(LogViewBridge, msg.message) ) -> {
                    val logMessage : LogViewMessage = MessageChannelHandler.receiveMessage(LogViewBridge, msg.extraInfo)

                    when {
                        (logMessage.messageType == LogViewMessage.LogViewMessageType.ResetTime) -> {
                            updateInstImmersionTime(initTimeStr)
                            updateCumlImmersionTime(initTimeStr)
                            updateLocalTime(initTimeStr)
                            rebuildTextTimeReadout()
                        }
                        else -> if (logMessage.message != null) {
                            if (logMessage.messageType == LogViewMessage.LogViewMessageType.LogEntry) {
                                addLog(logMessage.message)
                                build()
                            } else {
                                when (logMessage.messageType) {
                                    LogViewMessage.LogViewMessageType.InstImmersionTime -> updateInstImmersionTime(logMessage.message)
                                    LogViewMessage.LogViewMessageType.CumlImmersionTime -> updateCumlImmersionTime(logMessage.message)
                                    LogViewMessage.LogViewMessageType.LocalTime -> updateLocalTime(logMessage.message)
                                    else -> {}
                                }
                                rebuildTextTimeReadout()
                            }

                        }
                    }

                    return true
                }
            }
        }
        return false
    }
}