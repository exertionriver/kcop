package river.exertion.kcop.narrative.sequence

import kotlinx.serialization.Serializable
import river.exertion.kcop.narrative.NarrativeText
import river.exertion.kcop.system.Id

@Serializable
data class NarrativeSequenceBlock(
    override val narrativeText: String = "",
    override val sequenceNumber: Long = 0L,
    override val id: String = ""
) : Id, Sequence, NarrativeText {
}