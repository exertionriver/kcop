package river.exertion.kcop.system.messaging.messages

import river.exertion.kcop.system.ecs.component.ProfileSetting
import river.exertion.kcop.system.profile.Profile

data class ProfileMessage(val profileMessageType : ProfileMessageType, val profile : Profile? = null, val immersionId : String? = null, val settings : MutableList<ProfileSetting>? = null) {

    enum class ProfileMessageType {
        UpdateImmersionId, ReplaceCumlTimer, UpdateSettings, UpdateProfile, Inactivate,
    }
}
