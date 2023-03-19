package river.exertion.kcop.system.immersionTimer

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import river.exertion.kcop.system.messaging.MessageChannel
import river.exertion.kcop.system.messaging.messages.ImmersionTimerMessage

enum class ImmersionTimerState : State<ImmersionTimer> {

    RUNNING {
        override fun update(immersionTimer : ImmersionTimer) {
            immersionTimer.stateMachine.changeState(PAUSED)
            immersionTimer.pauseTimer()
        }
        override fun toggleState() = PAUSED
    },
    PAUSED {
        override fun update(immersionTimer : ImmersionTimer) {
            immersionTimer.stateMachine.changeState(RUNNING)
            immersionTimer.resumeTimer()
        }
        override fun toggleState()  = RUNNING
    }
    ;

    abstract override fun update(immersionTimer: ImmersionTimer)

    abstract fun toggleState() : ImmersionTimerState

    override fun enter(immersionTimer: ImmersionTimer?) {
    }

    override fun exit(immersionTimer: ImmersionTimer?) {
    }

    override fun onMessage(immersionTimer: ImmersionTimer?, telegram: Telegram?): Boolean {
        return true
    }

}
