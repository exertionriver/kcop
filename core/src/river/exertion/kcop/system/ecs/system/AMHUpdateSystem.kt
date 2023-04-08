package river.exertion.kcop.system.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ktx.ashley.oneOf
import river.exertion.kcop.system.messaging.MessageChannel
import river.exertion.kcop.system.ecs.component.NarrativeComponent
import river.exertion.kcop.system.ecs.component.ProfileComponent
import river.exertion.kcop.system.messaging.messages.AMHLoadMessage
import river.exertion.kcop.system.messaging.messages.AMHSaveMessage

class AMHUpdateSystem : IntervalIteratingSystem(oneOf(ProfileComponent::class, NarrativeComponent::class).get(), .1f) {

    override fun processEntity(entity: Entity) {

        val profileComponent = ProfileComponent.getFor(entity)
        val narrativeComponent = NarrativeComponent.getFor(entity)

        if ( profileComponent != null ) {
            profileComponent.profile?.cumlTime = profileComponent.cumlTime()
            MessageChannel.AMH_LOAD_BRIDGE.send(null, AMHLoadMessage(AMHLoadMessage.AMHLoadMessageType.RefreshCurrentProfile, null, profileComponent))
        }
        if ( narrativeComponent != null ) {
            narrativeComponent.narrativeImmersion?.location?.immersionBlockId = narrativeComponent.narrativeCurrBlockId()
            narrativeComponent.narrativeImmersion?.location?.cumlImmersionTime = narrativeComponent.cumlImmersionTime()
            narrativeComponent.narrativeImmersion?.blockImmersionTimers = narrativeComponent.blockImmersionTimersStr()
            MessageChannel.AMH_LOAD_BRIDGE.send(null, AMHLoadMessage(AMHLoadMessage.AMHLoadMessageType.RefreshCurrentImmersion, null, narrativeComponent))
       }
    }
}