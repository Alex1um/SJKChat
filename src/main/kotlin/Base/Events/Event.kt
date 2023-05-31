package Base.Events

import java.io.Serializable
import java.time.LocalDateTime

abstract class Event(
    val subject: String,
    val timestamp: String = LocalDateTime.now().toString(),
): Serializable {
    open fun getMessage(): String? {
        return null
    }
}