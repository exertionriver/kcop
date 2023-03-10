package river.exertion.kcop.system.messaging.messages

data class LogViewMessage(val messageType : LogViewMessageType, val message : String, val param : String? = null)

enum class LogViewMessageType {
    LogEntry, InstImmersionTime, CumlImmersionTime, LocalTime
}