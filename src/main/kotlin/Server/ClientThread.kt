package Server

import Base.Events.ConnectionEvent
import Base.Events.DisconnectionEvent
import Base.Events.MessageEvent
import Base.JSONObjectInputStream
import Base.JSONObjectOutputStream
import Base.Messages.*
import java.io.*
import java.net.Socket
import java.util.logging.Logger

class ClientThread(
    private val clientSocket: Socket,
    private val userManager: UserManager,
    private val eventQueue: EventQueue
) : Thread() {
    private lateinit var messageReader: ObjectInputStream
    private lateinit var messageWriter: ObjectOutputStream
    private lateinit var clientName: String

    companion object {
        private val logger = Logger.getLogger(ClientThread::class.java.name)
    }

    override fun run() {
        try {
            if (Config.SERILIZATION.equals("json")) {
                messageWriter = JSONObjectOutputStream(clientSocket.getOutputStream())
                messageReader = JSONObjectInputStream(clientSocket.getInputStream())
            } else {
                messageWriter = ObjectOutputStream(clientSocket.getOutputStream())
                messageReader = ObjectInputStream(clientSocket.getInputStream())
            }
            if (Config.LOGGING)
                logger.info("new connection $clientSocket")
            val initial = input() as InitialMessage
            clientName = initial.username
            if (Config.LOGGING)
                logger.info("got username: $clientName")
            eventQueue.addEvent(ConnectionEvent(clientName))
            this.send(eventQueue.getMessages(Config.INITIAL_MESSAGE_COUNT))
            serve()
        } catch (e: Exception) {
            if (Config.LOGGING)
                logger.warning("connection error: $e")
        } finally {
            clientSocket.close()
        }
    }

    private fun serve() {
        try {
            if (Config.LOGGING)
                logger.info("started serving...")
            userManager.addUser(this)

            while (true) {
                val obj = input()
                when (obj) {
                    is TextMessage -> {
                        val ev = MessageEvent(clientName, obj)
                        if (Config.LOGGING)
                            logger.info("new message: ${ev.getMessage()}")
                        eventQueue.addEvent(ev)
                    }

                    is CommandMessage -> {
                        if (Config.LOGGING)
                            logger.info("new command: $obj")
                        when (obj.command) {
                            Command.GET_USERS -> {
                                send(
                                    CommandMessageAnswer(
                                        obj.command,
                                        userManager.users
                                            .map { it.clientName }
                                            .joinToString(separator = ",")
                                    )
                                )
                            }
                            Command.PING -> {
                                send(
                                    CommandMessageAnswer(
                                        obj.command, ""
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: EOFException) {
            if (Config.LOGGING)
                logger.info("client disconnected: $e")
        } catch (e: Exception) {
            if (Config.LOGGING)
                logger.warning("error while communicating: $e")
        } finally {
            userManager.removeUser(this)
            eventQueue.addEvent(DisconnectionEvent(clientName))
        }
    }

    private fun input(): Any {
        return messageReader.readObject()
    }

    fun send(obj: Serializable) {
        messageWriter.writeObject(obj)
    }

    fun send(objs: List<Serializable>) {
        for (obj in objs) {
            messageWriter.writeObject(obj)
        }
    }
}