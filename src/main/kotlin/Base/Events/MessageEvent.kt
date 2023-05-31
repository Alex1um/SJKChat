package Base.Events

import Base.Messages.TextMessage

class MessageEvent(
    sender: String,
    val textMessage: TextMessage
): Event(sender) {
    override fun getMessage(): String {
        return "${timestamp} ${subject}: ${textMessage.message}"
    }
}