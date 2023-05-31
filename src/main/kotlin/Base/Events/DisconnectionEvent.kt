package Base.Events

class DisconnectionEvent(
    username: String
): Event(username) {
    override fun getMessage(): String {
        return "User $subject left the chat at $timestamp"
    }
}