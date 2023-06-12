package immersionTimer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import river.exertion.kcop.asset.immersionTimer.ImmersionTimer
import river.exertion.kcop.asset.immersionTimer.ImmersionTimerState

class TestImmersionTimerState {

    @Test
    fun testTimerStateTransition() {

        val testTimer = ImmersionTimer()

        println("current state: ${testTimer.stateMachine.currentState}")
        assertEquals(testTimer.stateMachine.currentState, ImmersionTimerState.PAUSED)

        testTimer.stateMachine.update()

        println("update(), current state: ${testTimer.stateMachine.currentState}")
        assertEquals(testTimer.stateMachine.currentState, ImmersionTimerState.RUNNING)

        testTimer.stateMachine.update()

        println("update(), current state: ${testTimer.stateMachine.currentState}")
        assertEquals(testTimer.stateMachine.currentState, ImmersionTimerState.PAUSED)
    }
}