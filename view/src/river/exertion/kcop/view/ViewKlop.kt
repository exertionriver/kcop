package river.exertion.kcop.view

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import river.exertion.kcop.asset.AssetManagerHandler
import river.exertion.kcop.asset.klop.IAssetKlop
import river.exertion.kcop.asset.view.ColorPalette
import river.exertion.kcop.base.Id
import river.exertion.kcop.base.KcopBase
import river.exertion.kcop.ecs.EngineHandler
import river.exertion.kcop.ecs.klop.IECSKlop
import river.exertion.kcop.messaging.MessageChannel
import river.exertion.kcop.messaging.MessageChannelHandler
import river.exertion.kcop.messaging.klop.IMessagingKlop
import river.exertion.kcop.view.asset.*
import river.exertion.kcop.view.klop.IDisplayViewKlop
import river.exertion.kcop.view.klop.IMenuKlop
import river.exertion.kcop.view.menu.DisplayViewMenuHandler
import river.exertion.kcop.view.menu.MainMenu
import river.exertion.kcop.view.messaging.KcopSimulationMessage
import river.exertion.kcop.view.plugin.DisplayViewPluginAsset
import river.exertion.kcop.view.plugin.DisplayViewPluginAssetLoader
import river.exertion.kcop.view.plugin.DisplayViewPluginAssets
import river.exertion.kcop.view.system.TimeLogSystem

object ViewKlop : IMessagingKlop, IAssetKlop, IECSKlop, IMenuKlop {

    override val id = Id.randomId()
    override val tag = this::class.simpleName.toString()
    override val name = KcopBase.appName
    override val version = KcopBase.appVersion

    override fun load() {
        loadChannels()
        loadAssets()
        loadSystems()
        loadMenus()

        KcopBase.inputMultiplexer.addProcessor(KcopBase.stage)
        KcopBase.inputMultiplexer.addProcessor(KcopInputProcessor)
    }

    override fun unload() {}

    override fun loadChannels() {
        MessageChannelHandler.addChannel(MessageChannel(KcopBridge, KcopSimulationMessage::class))
    }

    override fun loadAssets() {
        //internal fonts
        AssetManagerHandler.assets.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(AssetManagerHandler.ifhr))
        AssetManagerHandler.assets.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(AssetManagerHandler.ifhr))
        FreeTypeFontAssetStore.loadAll() //for internal assets
        FreeTypeFontAssets.reload() // for external assets

        KcopFont.TEXT.font = FreeTypeFontAssetStore.NotoSansSymbolsSemiBoldText.get()
        KcopFont.SMALL.font = FreeTypeFontAssetStore.ImmortalSmall.get()
        KcopFont.MEDIUM.font = FreeTypeFontAssetStore.ImmortalMedium.get()
        KcopFont.LARGE.font = FreeTypeFontAssetStore.ImmortalLarge.get()

        TextureAssetStore.loadAll()
        SkinAssetStore.loadAll()
        SoundAssetStore.loadAll()
        MusicAssetStore.loadAll()

        KcopSkin.skin = SkinAssetStore.KcopUi.get()
        KcopSkin.uiSounds[KcopSkin.UiSounds.Click] = SoundAssetStore.Click.get()
        KcopSkin.uiSounds[KcopSkin.UiSounds.Enter] = SoundAssetStore.Enter.get()
        KcopSkin.uiSounds[KcopSkin.UiSounds.Swoosh] = SoundAssetStore.Swoosh.get()

        //display view plugins
        AssetManagerHandler.assets.setLoader(DisplayViewPluginAsset::class.java, DisplayViewPluginAssetLoader(AssetManagerHandler.lfhr) )
    }

    override fun loadSystems() {
        EngineHandler.addSystem(TimeLogSystem())
    }

    override fun unloadSystems() { }

    override fun loadMenus() {
        DisplayViewMenuHandler.addMenu(MainMenu)
        DisplayViewMenuHandler.currentMenuTag = MainMenu.tag
    }

    override fun dispose() {
        SdcHandler.dispose()
        KcopSkin.dispose()

        super<IMessagingKlop>.dispose()
        super<IAssetKlop>.dispose()
        super<IECSKlop>.dispose()
    }

    const val KcopBridge = "KcopBridge"
    val MainMenuBackgroundColor = ColorPalette.of("Color101")
    val MainMenuText = ColorPalette.of("Color443")
}