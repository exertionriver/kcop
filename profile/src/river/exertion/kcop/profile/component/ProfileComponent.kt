package river.exertion.kcop.profile.component

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import river.exertion.kcop.ecs.ECSPackage.EngineComponentBridge
import river.exertion.kcop.ecs.component.IComponent
import river.exertion.kcop.ecs.component.ImmersionTimerComponent
import river.exertion.kcop.ecs.entity.SubjectEntity
import river.exertion.kcop.ecs.messaging.EngineComponentMessage
import river.exertion.kcop.messaging.MessageChannelHandler
import river.exertion.kcop.plugin.immersionTimer.ImmersionTimer
import river.exertion.kcop.plugin.immersionTimer.ImmersionTimerPair
import river.exertion.kcop.profile.Profile
import river.exertion.kcop.profile.ProfilePackage
import river.exertion.kcop.profile.ProfilePackage.ProfileBridge
import river.exertion.kcop.profile.messaging.ProfileComponentMessage
import river.exertion.kcop.profile.settings.ProfileSettingEntry

class ProfileComponent : IComponent, Telegraph {

    var profile : Profile
        get() = ProfilePackage.currentProfileAsset.profile
        set(value) { ProfilePackage.currentProfileAsset.profile = value }

    override fun componentId() = profile.id

    override var isInitialized = false

    val timerPair = ImmersionTimerPair(ImmersionTimer(), ImmersionTimer())

    var settings : MutableList<ProfileSettingEntry>
        get() = profile.settingEntries
        set(value) { profile.settingEntries = value }

    var cumlTime : String
        get() = profile.cumlTime
        set(value) { profile.cumlTime = value }

    fun cumlComponentTime() = if (isInitialized) timerPair.cumlImmersionTimer.immersionTime() else ImmersionTimer.CumlTimeZero

    override fun initialize(initData : Any?) {

        super.initialize(initData)

        timerPair.cumlImmersionTimer.setPastStartTime(ImmersionTimer.inMilliseconds(profile.cumlTime))

        activate()
    }

    fun activate() {
        if (isInitialized) {

            timerPair.instImmersionTimer.resetTimer()
            timerPair.instImmersionTimer.resumeTimer()
            timerPair.cumlImmersionTimer.resumeTimer()

            MessageChannelHandler.enableReceive(ProfileBridge, this)
        }
    }

    fun inactivate() {
        if (isInitialized) {

            timerPair.instImmersionTimer.pauseTimer()
            timerPair.cumlImmersionTimer.pauseTimer()

            MessageChannelHandler.disableReceive(ProfileBridge, this)

            isInitialized = false
        }
    }

    @Suppress("NewApi")
    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
            when {
                (MessageChannelHandler.isType(ProfileBridge, msg.message)) -> {
                    val profileComponentMessage: ProfileComponentMessage = MessageChannelHandler.receiveMessage(ProfileBridge, msg.extraInfo)

                    if (isValid(this)) {
                        when (profileComponentMessage.profileMessageType) {
                            ProfileComponentMessage.ProfileMessageType.ReplaceCumlTimer -> {
                                MessageChannelHandler.send(
                                    EngineComponentBridge, EngineComponentMessage(
                                        EngineComponentMessage.EngineComponentMessageType.ReplaceComponent,
                                        SubjectEntity.entityName, ImmersionTimerComponent::class.java, timerPair)
                                )
                            }
                            ProfileComponentMessage.ProfileMessageType.Inactivate -> {
                                inactivate()
                            }
                        }
                        return true
                    }
                }
            }
        }
        return false
    }

    companion object {
        fun has(entity : Entity?) : Boolean = entity?.components?.firstOrNull{ it is ProfileComponent } != null
        fun getFor(entity : Entity?) : ProfileComponent? = if (has(entity)) entity?.components?.firstOrNull { it is ProfileComponent } as ProfileComponent else null

        fun ecsInit() {
            MessageChannelHandler.send(
                    EngineComponentBridge, EngineComponentMessage(
                    EngineComponentMessage.EngineComponentMessageType.ReplaceComponent,
                    SubjectEntity.entityName, ProfileComponent::class.java)
            )
        }

        fun isValid(profileComponent: ProfileComponent?) : Boolean {
            return (profileComponent?.profile != null && profileComponent.isInitialized)
        }
    }
}