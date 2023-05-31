package Server

import java.net.ServerSocket

class Server(private val port: Int) : Thread() {
    private val userManager = UserManager()
    private val eventQueue = EventQueue(userManager)

    override fun run() {
        val serverSocket = ServerSocket(port)

        while (true) {
            val socket = serverSocket.accept()
            socket.soTimeout = Config.SOCKET_TIMEOUT
            val client = ClientThread(socket, userManager, eventQueue)
            client.start()
        }
    }
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val port = if (args.isNotEmpty()) args[0].toInt() else 48666
            val server = Server(port)
            server.start()
            println("Server started on port $port")
        }

    }
}