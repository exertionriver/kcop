package river.exertion.kcop

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import kotlin.reflect.KClass

enum class MessageChannel {

    COLOR_PALETTE_BRIDGE { override val messageClass = ColorPaletteMessage::class }
    ;

    fun id() = this.ordinal
    abstract val messageClass : KClass<*>
    fun send(sender: Telegraph?, message : Any) = if (this.messageClass.isInstance(message)) MessageManager.getInstance().dispatchMessage(sender, this.id(), message) else throw Exception("send:$this requires ${this.messageClass}, found ${message::class}")
    fun enableReceive(receiver: Telegraph?) = MessageManager.getInstance().addListener(receiver, this.id())
    inline fun <reified T:Any> receiveMessage(message : Any) : T {
        return if (T::class == this.messageClass) message as T else throw Exception("receive:$this requires ${this.messageClass}, found ${T::class}")
    }
}