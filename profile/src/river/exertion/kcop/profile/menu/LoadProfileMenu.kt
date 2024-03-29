package river.exertion.kcop.profile.menu

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import river.exertion.kcop.ecs.component.IComponent
import river.exertion.kcop.messaging.MessageChannelHandler
import river.exertion.kcop.profile.ProfileKlop
import river.exertion.kcop.profile.ProfileKlop.NoProfileInfoFound
import river.exertion.kcop.profile.ProfileKlop.ProfileMenuBackgroundColor
import river.exertion.kcop.profile.ProfileKlop.ProfileMenuText
import river.exertion.kcop.profile.asset.ProfileAsset
import river.exertion.kcop.profile.component.ProfileComponent
import river.exertion.kcop.profile.messaging.ProfileComponentMessage
import river.exertion.kcop.view.KcopSkin
import river.exertion.kcop.view.KcopFont
import river.exertion.kcop.view.layout.ButtonView
import river.exertion.kcop.view.menu.DisplayViewMenu
import river.exertion.kcop.view.menu.DisplayViewMenuHandler
import river.exertion.kcop.view.menu.MainMenu
import river.exertion.kcop.view.menu.MenuActionParam

object LoadProfileMenu : DisplayViewMenu {

    override val tag = "loadProfileMenu"
    override val label = "Load"

    override val backgroundColor = ProfileMenuBackgroundColor

    override var menuPane = {
        Table().apply {

            if (ProfileAsset.selectedProfileAsset.assetInfo().isNotEmpty()) {
                ProfileAsset.selectedProfileAsset.assetInfo().forEach { profileEntry ->
                    this.add(Label(profileEntry, KcopSkin.labelStyle(KcopFont.SMALL, ProfileMenuText))
                        .apply {
                            this.wrap = true
                        }).growX().left()
                    this.row()
                }
                this@LoadProfileMenu.assignableActions.firstOrNull { it.label == "Yes" }
                    ?.apply { this.log = "Profile Loaded : ${ProfileAsset.selectedProfileAsset.assetName()}" }
            } else {
                this.add(
                    Label(NoProfileInfoFound, KcopSkin.labelStyle(KcopFont.SMALL, ProfileMenuText))
                ).growX().left()
                this@LoadProfileMenu.assignableActions.firstOrNull { it.label == "Yes" }
                    ?.apply { this.label = "Error"; this.action = {} }
            }
            this.top()
        }
    }
    override val breadcrumbEntries = mapOf(
        ProfileMenu.tag to ProfileMenu.label,
        MainMenu.tag to MainMenu.label
    )

    override val assignableNavs = mutableListOf<MenuActionParam>()

    override val assignableActions = mutableListOf(
        MenuActionParam("Yes", {
            ButtonView.closeMenu()
            MessageChannelHandler.send(ProfileKlop.ProfileBridge, ProfileComponentMessage(ProfileComponentMessage.ProfileMessageType.Inactivate))
            ProfileAsset.currentProfileAsset = ProfileAsset.selectedProfileAsset
            IComponent.ecsInit<ProfileComponent>()
        }, "Profile Loaded!"),
        //go back a menu
        MenuActionParam("No", {
            DisplayViewMenuHandler.currentMenuTag = breadcrumbEntries.keys.toList()[0]
        })
    )
}