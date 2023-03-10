package river.exertion.kcop.system.view

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import river.exertion.kcop.simulation.view.ViewType
import river.exertion.kcop.system.messaging.MessageChannel
import river.exertion.kcop.system.messaging.messages.InputViewMessage
import river.exertion.kcop.system.messaging.messages.InputViewMessageEvent
import river.exertion.kcop.system.messaging.messages.InputViewMessageParam
import river.exertion.kcop.system.messaging.messages.ViewMessage

class ViewInputProcessor : InputProcessor {

    override fun keyDown(keycode: Int): Boolean {
        MessageChannel.NARRATIVE_PROMPT_BRIDGE_PAUSE_GATE.send(null, ViewMessage(ViewType.TEXT, Input.Keys.toString(keycode)))

//        MessageChannel.LOG_VIEW_BRIDGE.send(null, LogViewMessage(LogViewMessageType.LogEntry, "keyDown event:${Input.Keys.toString(keycode)}"))

        MessageChannel.INPUT_VIEW_BRIDGE.send(null, InputViewMessage(
            event = InputViewMessageEvent.KeyDownEvent,
            eventParams = mapOf(InputViewMessageParam.KeycodeStrKey to Input.Keys.toString(keycode)))
        )
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        MessageChannel.INPUT_VIEW_BRIDGE.send(null, InputViewMessage(
            event = InputViewMessageEvent.KeyUpEvent,
            eventParams = mapOf(InputViewMessageParam.KeycodeStrKey to Input.Keys.toString(keycode)))
        )
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        MessageChannel.INPUT_VIEW_BRIDGE.send(null, InputViewMessage(
            event = InputViewMessageEvent.KeyTypedEvent,
            eventParams = mapOf(InputViewMessageParam.CharacterKey to character))
        )
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        MessageChannel.INPUT_VIEW_BRIDGE.send(null, InputViewMessage(
            event = InputViewMessageEvent.TouchDownEvent,
            eventParams = mapOf(
                InputViewMessageParam.ScreenXKey to screenX,
                InputViewMessageParam.ScreenYKey to screenY,
                InputViewMessageParam.PointerKey to pointer,
                InputViewMessageParam.ButtonKey to button)
            )
        )
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        MessageChannel.INPUT_VIEW_BRIDGE.send(null, InputViewMessage(
            event = InputViewMessageEvent.TouchUpEvent,
            eventParams = mapOf(
                InputViewMessageParam.ScreenXKey to screenX,
                InputViewMessageParam.ScreenYKey to screenY,
                InputViewMessageParam.PointerKey to pointer,
                InputViewMessageParam.ButtonKey to button)
            )
        )
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        MessageChannel.INPUT_VIEW_BRIDGE.send(null, InputViewMessage(
            event = InputViewMessageEvent.TouchDraggedEvent,
            eventParams = mapOf(
                InputViewMessageParam.ScreenXKey to screenX,
                InputViewMessageParam.ScreenYKey to screenY,
                InputViewMessageParam.PointerKey to pointer)
            )
        )
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        MessageChannel.INPUT_VIEW_BRIDGE.send(null, InputViewMessage(
            event = InputViewMessageEvent.MouseMovedEvent,
            eventParams = mapOf(
                InputViewMessageParam.ScreenXKey to screenX,
                InputViewMessageParam.ScreenYKey to screenY)
            )
        )
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        MessageChannel.INPUT_VIEW_BRIDGE.send(null, InputViewMessage(
            event = InputViewMessageEvent.ScrolledEvent,
            eventParams = mapOf(
                InputViewMessageParam.AmountXKey to amountX,
                InputViewMessageParam.AmountYKey to amountY)
            )
        )
        return false
    }
}