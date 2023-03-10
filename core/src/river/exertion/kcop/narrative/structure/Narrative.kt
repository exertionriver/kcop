package river.exertion.kcop.narrative.structure

import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ktx.assets.Asset
import river.exertion.kcop.Id
import river.exertion.kcop.assets.FontSize

@Serializable
data class Narrative(
    override var id: String,
    val layoutTag : String,
    val narrativeBlocks : MutableList<NarrativeBlock> = mutableListOf(),
    val promptBlocks : MutableList<PromptBlock> = mutableListOf(),
    val eventBlocks : MutableList<EventBlock> = mutableListOf(),
    val timelineEvents : MutableList<TimelineEvent> = mutableListOf(),
    val timelineEventBlocks : MutableList<TimelineEventBlock> = mutableListOf(),
    ) : Id() {

    @Transient
    var currentId = ""

    @Transient
    var previousId = ""

    @Transient
    var textures : MutableMap<String, Asset<Texture>> = mutableMapOf()

    @Transient
    var sounds : MutableMap<String, Asset<Music>> = mutableMapOf()

    @Transient
    var music : MutableMap<String, Asset<Music>> = mutableMapOf()


    fun init() {
        currentId = narrativeBlocks.firstOrNull()?.id ?: currentId
        previousId = currentId
    }

    fun currentBlock() = narrativeBlocks.firstOrNull { it.id == currentId }

    fun previousBlock() = narrativeBlocks.firstOrNull { it.id == previousId }

    fun currentIdx() = narrativeBlocks.indexOf(currentBlock() ?: narrativeBlocks[0])

    fun previousIdx() = narrativeBlocks.indexOf(previousBlock() ?: narrativeBlocks[0])

    fun seqPrevIdx() = (currentIdx() - 1).coerceAtLeast(0)

    fun seqNextIdx() = (currentIdx() + 1).coerceAtMost(narrativeBlocks.size - 1)

    fun isSequential() : Boolean = promptBlocks.isEmpty()

    fun currentText() = currentBlock()?.toTextString() ?: ""

    fun currentDisplayText() = currentBlock()?.toDisplayString() ?: ""

    fun currentFontSize() = FontSize.byTag(currentBlock()?.fontSize)

    fun currentPrompts() : List<String> =
        if (isSequential()) {
            listOf("(???) Prev", "(???) Next")
        } else {
            promptBlocks.firstOrNull { it.narrativeBlockId == currentId }?.prompts?.map { it.promptText } ?: listOf("<narrativePrompts not found for '$id' at '$currentId')>")
        }

    fun previousEventBlock() = eventBlocks.firstOrNull { it.narrativeBlockId == previousId }
    fun currentEventBlock() = eventBlocks.firstOrNull { it.narrativeBlockId == currentId }

    fun currentTimelineEventBlock() = timelineEventBlocks.firstOrNull { it.narrativeBlockId == currentId }

    fun next(promptKey : String) {
        val possiblePreviousId = currentId

        currentId = if (isSequential()) {
            when (promptKey) {
                Input.Keys.toString(Input.Keys.UP) -> narrativeBlocks[seqPrevIdx()].id
                Input.Keys.toString(Input.Keys.DOWN) -> narrativeBlocks[seqNextIdx()].id
                else -> currentId
            }
        } else {
            promptBlocks.firstOrNull { it.narrativeBlockId == currentId }?.prompts?.firstOrNull { it.promptKey.toString() == promptKey }?.promptNextId ?: currentId
        }

        if (currentId != possiblePreviousId) previousId = possiblePreviousId
    }

    fun prev() {
        val possiblePreviousId = currentId
        currentId = narrativeBlocks[seqPrevIdx()].id
        if (currentId != possiblePreviousId) previousId = possiblePreviousId
    }

    fun next() {
        val possiblePreviousId = currentId
        currentId = narrativeBlocks[seqNextIdx()].id
        if (currentId != possiblePreviousId) previousId = possiblePreviousId
    }
}