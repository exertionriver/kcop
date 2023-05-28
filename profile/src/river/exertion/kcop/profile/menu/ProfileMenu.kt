package river.exertion.kcop.profile.menu

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.collections.toGdxArray
import river.exertion.kcop.asset.view.ColorPalette
import river.exertion.kcop.profile.Profile
import river.exertion.kcop.profile.asset.ProfileAsset
import river.exertion.kcop.profile.asset.ProfileAssets
import river.exertion.kcop.view.KcopSkin
import river.exertion.kcop.view.menu.DisplayViewMenu
import river.exertion.kcop.view.menu.DisplayViewMenuHandler
import river.exertion.kcop.view.menu.MainMenu
import river.exertion.kcop.view.menu.MenuActionParam

object ProfileMenu : DisplayViewMenu {

    override val tag = "profileMenu"
    override val label = "Profile"

    override val backgroundColor = ColorPalette.of("green")

    override var menuPane = {

        val listCtrl = com.badlogic.gdx.scenes.scene2d.ui.List<String>(KcopSkin.skin)
        val profileAssetsMap = ProfileAssets.reload().associateBy { it.assetTitle() }

        if (profileAssetsMap.isNotEmpty()) {
            ProfileAsset.selectedProfileAsset = profileAssetsMap.entries.first().value

            listCtrl.onChange {
                ProfileAsset.selectedProfileAsset = profileAssetsMap.values.toList()[this.selectedIndex]
            }

            listCtrl.setItems(profileAssetsMap.keys.toGdxArray())
        } else {
            listCtrl.setItems(listOf("no profiles found").toGdxArray() )
        }

        listCtrl.alignment = Align.topLeft

        Table().apply {
            this.add(listCtrl).growY().top().left()
            this.top()
            this.left()
        }
    }
    override val breadcrumbEntries = mapOf(
        MainMenu.tag to MainMenu.label
    )

    override val assignableNavs = mutableListOf(
            MenuActionParam("Load >", {
                DisplayViewMenuHandler.currentMenuTag = LoadProfileMenu.tag
            }),
            MenuActionParam("New >", {
                NewProfileMenu.newName = Profile.genName()
                DisplayViewMenuHandler.currentMenuTag = NewProfileMenu.tag
            })
    )

    override val actions = mutableListOf<MenuActionParam>()
}