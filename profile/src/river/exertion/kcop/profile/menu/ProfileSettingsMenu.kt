package river.exertion.kcop.profile.menu

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.actors.onChange
import ktx.collections.toGdxArray
import river.exertion.kcop.profile.Profile
import river.exertion.kcop.profile.ProfileKlop
import river.exertion.kcop.profile.ProfileKlop.ProfileMenuBackgroundColor
import river.exertion.kcop.profile.asset.ProfileAsset
import river.exertion.kcop.profile.settings.ProfileSettingEntry
import river.exertion.kcop.profile.settings.ProfileSettingEntry.Companion.optionBySelectionKey
import river.exertion.kcop.profile.settings.ProfileSettingEntry.Companion.updateSetting
import river.exertion.kcop.view.KcopSkin
import river.exertion.kcop.view.KcopFont
import river.exertion.kcop.view.layout.ButtonView
import river.exertion.kcop.view.menu.DisplayViewMenu
import river.exertion.kcop.view.menu.DisplayViewMenuHandler
import river.exertion.kcop.view.menu.MainMenu
import river.exertion.kcop.view.menu.MenuActionParam

object ProfileSettingsMenu : DisplayViewMenu {

    override val tag = "profileSettingsMenu"
    override val label = "Profile Settings"

    override val backgroundColor = ProfileMenuBackgroundColor

    var settings : MutableList<ProfileSettingEntry> = mutableListOf()

    override var menuPane = {
        Table().apply {
            Profile.availableSettings.forEach { setting ->
                this.add(Label(setting.selectionLabel, KcopSkin.labelStyle(
                    KcopFont.SMALL,
                    ProfileKlop.ProfileMenuText
                )).apply {
                    this.wrap
                } ).left()

                this.add(
                    SelectBox<String>(KcopSkin.skin
                ).apply {
                    this.items = setting.options.map { it.optionValue }.toGdxArray()
                    this.selected = settings.optionBySelectionKey(setting.selectionKey)
                    this.onChange { settings.updateSetting(setting.selectionKey, this.selected) }
                }).right()
                this.row()
            }

            this.top().left()
        }
    }

    override val breadcrumbEntries = mapOf(
        MainMenu.tag to MainMenu.label
    )

    override val assignableNavs = mutableListOf<MenuActionParam>()

    override val assignableActions = mutableListOf(
        MenuActionParam("Update", {
            ButtonView.closeMenu()
            ProfileAsset.currentProfileAsset.settings = settings
            ProfileAsset.currentProfileAsset.profile.execSettings()

            DisplayViewMenuHandler.currentMenuTag = breadcrumbEntries.keys.toList()[0]
        }, "Settings Updated!"),
        MenuActionParam("Cancel", {
            DisplayViewMenuHandler.currentMenuTag = breadcrumbEntries.keys.toList()[0]
        })
    )
}