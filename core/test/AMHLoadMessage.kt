import river.exertion.kcop.ecs.component.IComponent

data class AMHLoadMessage(val messageType : AMHLoadMessageType, val selectedTitle : String? = null, val loadComponent: IComponent? = null) {

    enum class AMHLoadMessageType {
        ReloadMenuProfiles, ReloadMenuNarratives,
        RefreshCurrentProfile, RefreshCurrentImmersion,
        RemoveCurrentProfile, RemoveCurrentImmersion,
        SetSelectedProfileFromAsset, SetSelectedNarrativeFromAsset,
        InitSelectedProfile, InitSelectedNarrative,
        UpdateSelectedProfileFromComponents, UpdateSelectedNarrativeFromComponent
    }
}
