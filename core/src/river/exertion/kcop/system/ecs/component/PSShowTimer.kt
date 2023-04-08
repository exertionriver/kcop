package river.exertion.kcop.system.ecs.component

import river.exertion.kcop.system.messaging.MessageChannel
import river.exertion.kcop.system.messaging.messages.NarrativeMessage
import river.exertion.kcop.system.messaging.messages.ProfileMessage

object PSShowTimer : PSSelection {

    override val selectionKey = "showTimer"

    override val selectionLabel = "Show Timer"

    override val options = listOf (
        PSOption("Profile", PSShowTimerOptions.Profile.tag()),
        PSOption("Narrative", PSShowTimerOptions.Narrative.tag())
    )

    enum class PSShowTimerOptions {
        Profile { override fun tag() = "profile"
            override fun exec() {
                MessageChannel.PROFILE_BRIDGE.send(null, ProfileMessage(ProfileMessage.ProfileMessageType.ReplaceCumlTimer))
            }
        },
        Narrative { override fun tag() = "narrative"
            override fun exec() {
                MessageChannel.NARRATIVE_BRIDGE.send(null, NarrativeMessage(NarrativeMessage.NarrativeMessageType.ReplaceCumlTimer))
            }
        }
        ;
        abstract fun tag() : String
        abstract fun exec()

        companion object {
            fun byTag(tag : String) : PSShowTimerOptions? = values().firstOrNull { it.tag() == tag }
        }
    }
}