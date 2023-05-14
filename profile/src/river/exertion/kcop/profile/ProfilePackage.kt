package river.exertion.kcop.profile

import river.exertion.kcop.asset.AssetManagerHandler
import river.exertion.kcop.asset.AssetManagerHandler.lfhr
import river.exertion.kcop.ecs.ECSPackage
import river.exertion.kcop.ecs.component.IRLTimeComponent
import river.exertion.kcop.ecs.component.ImmersionTimerComponent
import river.exertion.kcop.ecs.entity.SubjectEntity
import river.exertion.kcop.ecs.messaging.EngineComponentMessage
import river.exertion.kcop.messaging.Id
import river.exertion.kcop.messaging.MessageChannel
import river.exertion.kcop.messaging.MessageChannelHandler
import river.exertion.kcop.plugin.IKcopPackage
import river.exertion.kcop.profile.asset.ProfileAsset
import river.exertion.kcop.profile.asset.ProfileAssetLoader
import river.exertion.kcop.profile.asset.ProfileAssets
import river.exertion.kcop.profile.component.ProfileComponent
import river.exertion.kcop.profile.menu.*
import river.exertion.kcop.profile.messaging.ProfileMenuDataMessage
import river.exertion.kcop.profile.messaging.ProfileMessage
import river.exertion.kcop.view.ViewPackage.DisplayViewBridge
import river.exertion.kcop.view.menu.DisplayViewMenuHandler
import river.exertion.kcop.view.menu.MainMenu
import river.exertion.kcop.view.messaging.DisplayViewMessage
import river.exertion.kcop.view.messaging.menuParams.ActionParam

object ProfilePackage : IKcopPackage {
    override var id = Id.randomId()
    override var name = this::class.simpleName.toString()

    var profileAssets = ProfileAssets
    var selectedProfileAsset = ProfileAsset()
    var currentProfileAsset = ProfileAsset()
        set(value) {
            field = value

            MessageChannelHandler.send(
                    ECSPackage.EngineComponentBridge, EngineComponentMessage(
                    EngineComponentMessage.EngineComponentMessageType.ReplaceComponent,
                    SubjectEntity.entityName, ProfileComponent::class.java)
            )

            value.profile.execSettings()
        }

    override fun loadAssets() {
        AssetManagerHandler.assets.setLoader(ProfileAsset::class.java, ProfileAssetLoader(lfhr))
    }

    override fun loadChannels() {
        MessageChannelHandler.addChannel(MessageChannel(ProfileBridge, ProfileMessage::class))
        MessageChannelHandler.addChannel(MessageChannel(ProfileMenuDataBridge, ProfileMenuDataMessage::class))
    }

    override fun loadMenus() {
        DisplayViewMenuHandler.addMenu(LoadProfileMenu)
        DisplayViewMenuHandler.addMenu(NewProfileMenu)
        DisplayViewMenuHandler.addMenu(ProfileMenu)
        DisplayViewMenuHandler.addMenu(ProfileSettingsMenu)
        DisplayViewMenuHandler.addMenu(RestartProgressMenu)
        DisplayViewMenuHandler.addMenu(SaveProgressMenu)

        MainMenu.assignableNavs.add(
            ActionParam("Profile >", {
                DisplayViewMenuHandler.currentMenuTag = ProfileMenu.tag
                MessageChannelHandler.send(DisplayViewBridge, DisplayViewMessage(DisplayViewMessage.DisplayViewMessageType.Rebuild) )

            }))
        MainMenu.assignableNavs.add(
            ActionParam("Settings >", {
                ProfileSettingsMenu.settings = currentProfileAsset.settings
                DisplayViewMenuHandler.currentMenuTag = ProfileSettingsMenu.tag
                MessageChannelHandler.send(DisplayViewBridge, DisplayViewMessage(DisplayViewMessage.DisplayViewMessageType.Rebuild) )
            }))
        MainMenu.assignableNavs.add(
            ActionParam("Save Progress >", {
                DisplayViewMenuHandler.currentMenuTag = SaveProgressMenu.tag
                MessageChannelHandler.send(DisplayViewBridge, DisplayViewMessage(DisplayViewMessage.DisplayViewMessageType.Rebuild) )
            }))
    }

    override fun loadSystems() {
        MessageChannelHandler.send(
                ECSPackage.EngineComponentBridge, EngineComponentMessage(
                EngineComponentMessage.EngineComponentMessageType.ReplaceComponent,
                SubjectEntity.entityName, IRLTimeComponent::class.java)
        )
    }

    override fun dispose() {}

    const val ProfileBridge = "ProfileBridge"
    const val ProfileMenuDataBridge = "ProfileMenuDataBridge"
}