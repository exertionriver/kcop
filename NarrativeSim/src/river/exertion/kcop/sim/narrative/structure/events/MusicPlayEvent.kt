package river.exertion.kcop.sim.narrative.structure.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import river.exertion.kcop.messaging.MessageChannelHandler
import river.exertion.kcop.sim.narrative.messaging.NarrativeFlagsMessage
import river.exertion.kcop.sim.narrative.messaging.NarrativeFlagsMessage.Companion.NarrativeFlagsBridge
import river.exertion.kcop.sim.narrative.messaging.NarrativeMediaMessage
import river.exertion.kcop.sim.narrative.messaging.NarrativeMediaMessage.Companion.NarrativeMediaBridge
import river.exertion.kcop.sim.narrative.structure.NarrativeImmersion

@Serializable
@SerialName("playMusic")
class MusicPlayEvent(
    override val type: String = "",
    override val musicFile: String = "",
) : Event(), IMusicEvent {

    override fun execEvent(previousEvent : Event?) {
        MessageChannelHandler.send(NarrativeFlagsBridge, NarrativeFlagsMessage(NarrativeFlagsMessage.NarrativeFlagsMessageType.SetBlockFlag, "exec_${id!!}", NarrativeImmersion.EventFiredValue) )
        MessageChannelHandler.send(NarrativeMediaBridge, NarrativeMediaMessage(NarrativeMediaMessage.NarrativeMediaMessageType.PlayMusic, musicFile) )
    }

    override fun resolveEvent(currentEvent: Event?) {
        if (currentEvent == null) {
            MessageChannelHandler.send(NarrativeFlagsBridge, NarrativeFlagsMessage(NarrativeFlagsMessage.NarrativeFlagsMessageType.SetBlockFlag, "resolve_${id!!}", NarrativeImmersion.EventFiredValue) )
            MessageChannelHandler.send(NarrativeMediaBridge, NarrativeMediaMessage(NarrativeMediaMessage.NarrativeMediaMessageType.StopMusic, musicFile) )
        }
    }
}