package Server

import Base.Events.Event
import java.io.Serializable

class UserManager {
    val users = mutableListOf<ClientThread>()

    @Synchronized
    fun addUser(user: ClientThread) {
        users.add(user)
    }

    @Synchronized
    fun removeUser(user: ClientThread) {
        users.remove(user)
    }

    @Synchronized
    fun broadcast(e: Serializable) {
        for (user in users) {
            user.send(e)
        }
    }
}