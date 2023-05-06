package river.exertion.kcop.view.menu

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick
import river.exertion.kcop.messaging.MessageChannelHandler
import river.exertion.kcop.view.ColorPalette
import river.exertion.kcop.view.FontSize
import river.exertion.kcop.view.KcopSkin
import river.exertion.kcop.view.SdcHandler
import river.exertion.kcop.view.ViewPackage.LogViewBridge
import river.exertion.kcop.view.ViewPackage.MenuViewBridge
import river.exertion.kcop.view.layout.ViewType
import river.exertion.kcop.view.messaging.LogViewMessage
import river.exertion.kcop.view.messaging.MenuViewMessage
import river.exertion.kcop.view.messaging.menuParams.ActionParam

interface DisplayViewMenu {

    var sdcHandler : SdcHandler
    var kcopSkin : KcopSkin

    fun skin() = kcopSkin.skin

    var screenWidth : Float
    var screenHeight : Float

    val backgroundColor : ColorPalette

    val breadcrumbEntries : Map<String, String> //menu tags -> menu labels
    val navs : MutableList<ActionParam> //Button Label -> action
    val actions : MutableList<ActionParam> //Button Label -> log text + action to run

    fun menuPane() : Table?
    fun navButtonPane() : Table = Table().apply {
       // this.debug()

        this@DisplayViewMenu.navs.forEach { navEntry ->
            this.add(
                TextButton(navEntry.label, skin())
                        //TextButton.TextButtonStyle().apply { this.font = bitmapFont} )
                        .apply {
                    this.onClick {
                        navEntry.action()
                    this.center()
                    }
                }
            ).padTop(ViewType.padHeight(screenHeight))
            if (navEntry != this@DisplayViewMenu.navs.last()) this.row()
        }
    }

    fun actionButtonPane() : Table = Table().apply {
     //   this.debug()

        this@DisplayViewMenu.actions.forEach { actionEntry ->
            this.add(
                TextButton(actionEntry.label, skin())
                        //TextButton.TextButtonStyle().apply { this.font = bitmapFont} )
                        .apply {
                    this.onClick {
                        if (actionEntry.log != null)
                            MessageChannelHandler.send(LogViewBridge, LogViewMessage(LogViewMessage.LogViewMessageType.LogEntry, actionEntry.log!!))
                        actionEntry.action()
                    }
                }
            ).right().padRight(ViewType.padWidth(screenWidth))
        }
    }

    fun breadcrumbPane() = Table().apply {
      //  this.debug()

        //TODO : singleton with three-sized bitmap fonts
        breadcrumbEntries.entries.reversed().forEach { menuLabel ->
            this.add(Label("${menuLabel.value} > ", kcopSkin.labelStyle(FontSize.SMALL, backgroundColor.label()))
                    .apply {
                this.onClick {
                    MessageChannelHandler.send(MenuViewBridge, MenuViewMessage(menuLabel.key) )
                }
            } )
        }
        this.right()

        return this
    }

    fun menuColorTexture() : TextureRegion {
        return sdcHandler.get("menu_${tag()}", backgroundColor).textureRegion().apply {
            this.setRegion(0, 0, ViewType.secondWidth(screenWidth).toInt() - 1, ViewType.secondHeight(screenHeight).toInt() - 1)
        }
    }

    fun menuLayout() : Table {

        val menuPane = menuPane()

        return Table().apply {
            this.add(Stack().apply {
                this.add(Image(menuColorTexture()))
                this.add(
                    Table().apply {
                        this.add(breadcrumbPane()).right().growX()
                        this.add(
                            Table().apply {
                                this.add(Label(this@DisplayViewMenu.label(), kcopSkin.labelStyle(FontSize.MEDIUM, backgroundColor.label()))
                            .apply {
                                this.setAlignment(Align.center)
                            }).padRight(ViewType.padWidth(screenWidth))
                            }
                        ).center().right()
                        this.row()
                        this.add(
                            if (menuPane == null) { Table() }
                            else {
                                ScrollPane(menuPane, skin()).apply {
                                    // https://github.com/raeleus/skin-composer/wiki/ScrollPane
                                    this.fadeScrollBars = false
                                    this.setFlickScroll(false)
                                    this.validate()
                                    //https://gamedev.stackexchange.com/questions/96096/libgdx-scrollpane-wont-scroll-to-bottom-after-adding-children-to-contained-tab
                                    this.layout()
                                }
                            }
                        ).height(ViewType.secondHeight(screenHeight) - 3 * kcopSkin.fontPackage.large.lineHeight)
                            .width(ViewType.secondWidth(screenWidth) - 3 * kcopSkin.fontPackage.large.lineHeight)
                            .growY().top()
                        this.add(navButtonPane()).top()
                        this.row()
                        this.add(actionButtonPane()).colspan(2).right()
                  //      this.debug()
                    }
                )
            })
      //      this.debug()
        }
    }

    fun tag() : String //= tag //need to override this in implementing menu
    fun label() : String //= label //need to override this in implementing menu

    fun dispose() {
        sdcHandler.dispose()
        kcopSkin.dispose()
    }
}