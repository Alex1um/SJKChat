package Base.Events

class ConnectionEvent(
    username: String,
) : Event(username) {
    override fun getMessage(): String {
        return "User $subject connected to the chat at $timestamp"
    }
}