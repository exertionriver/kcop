package river.exertion.kcop.system.messaging.messages

data class InputViewMessage(val event : InputViewMessageEvent, val eventParams : Map<InputViewMessageParam, Any>) {

    fun getKeyStr(): String =
        eventParams[InputViewMessageParam.KeycodeStrKey]?.toString() ?: eventParams[InputViewMessageParam.CharacterKey]?.toString() ?: ""

    fun getScreenX(): Int = eventParams[InputViewMessageParam.ScreenXKey]?.toString()?.toInt() ?: 0

    fun getScreenY(): Int = eventParams[InputViewMessageParam.ScreenYKey]?.toString()?.toInt() ?: 0

    fun getButton(): Int = eventParams[InputViewMessageParam.ButtonKey]?.toString()?.toInt() ?: 0
}

enum class InputViewMessageEvent {
    KeyDownEvent, KeyUpEvent, KeyTypedEvent, TouchDownEvent, TouchUpEvent, TouchDraggedEvent, MouseMovedEvent, ScrolledEvent;

    fun isPressEvent() = listOf(KeyDownEvent, TouchDownEvent).contains(this)
    fun isReleaseEvent() = listOf(KeyUpEvent, TouchUpEvent).contains(this)

    fun isKeyEvent() = listOf(KeyDownEvent, KeyUpEvent, KeyTypedEvent).contains(this)
    fun isTouchEvent() = listOf(TouchDownEvent, TouchUpEvent, TouchDraggedEvent).contains(this)
}

enum class InputViewMessageParam {
    KeycodeStrKey, CharacterKey, ScreenXKey, ScreenYKey, PointerKey, ButtonKey, AmountXKey, AmountYKey
}
