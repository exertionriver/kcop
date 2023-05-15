package river.exertion.kcop.sim.narrative.structure.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import river.exertion.kcop.sim.narrative.NarrativePackage
import river.exertion.kcop.sim.narrative.structure.NarrativeState

@Serializable
@SerialName("minusCounter")
class CounterMinusEvent(
    override val type: String = "",
    override val trigger: String = "",
    val counterKey: String = ""
) : Event(), ITriggerEvent {

    override fun execEvent(previousEvent : Event?) {
        NarrativePackage.currentNarrativeStateAsset.narrativeState.setPersistFlag(id!!, NarrativeState.EventFiredValue)
        NarrativePackage.currentNarrativeStateAsset.narrativeState.addToCounter(counterKey, (-1).toString() )
    }
}
