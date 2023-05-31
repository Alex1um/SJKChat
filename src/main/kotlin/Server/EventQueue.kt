package Server

import Base.Events.Event
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

class EventQueue(
    val userManager: UserManager
) {
    private val events = ArrayList<Event>()

    @Synchronized
    fun addEvent(event: Event) {
        events.add(event)
        userManager.broadcast(event)
    }

    @Synchronized
    fun getMessages(n: Int): List<Event> {
        if (events.isEmpty()) return ArrayList()
        val count = min(n, events.size)
        return events.subList(events.size - count, events.size - 1)
    }

}