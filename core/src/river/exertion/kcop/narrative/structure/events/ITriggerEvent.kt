package river.exertion.kcop.narrative.structure.events

import river.exertion.kcop.system.immersionTimer.ImmersionTimer

interface ITriggerEvent {
    val trigger: String

    fun blockTrigger() : EventTrigger? = EventTrigger.values().firstOrNull { trigger == it.label() }
    fun timeTrigger() : String? = if (ImmersionTimer.isValidTime(trigger)) trigger else null

    enum class EventTrigger {
        ON_EXIT { override fun label() = "onExit"},
        ON_ENTRY {override fun label() = "onEntry"},
        ;
        abstract fun label() : String
    }

}