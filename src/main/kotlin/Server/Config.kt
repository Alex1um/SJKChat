package Server

import java.io.File

object Config {
    var INITIAL_MESSAGE_COUNT = 10
        private set
    var LOGGING = true
        private set
    var SERILIZATION = "json" // base / json
        private set
    var SOCKET_TIMEOUT = 10000 // base / json
        private set
    var PING_DELAY = 5000
        private set

    init {
        loadConfigFromFile("config.properties")
    }

    fun loadConfigFromFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.readLines().forEach {
                val (key, value) = it.split("=")
                when (key.trim()) {
                    "INITIAL_MESSAGE_COUNT" -> INITIAL_MESSAGE_COUNT = value.trim().toInt()
                    "LOGGING" -> LOGGING = value.trim().toBoolean()
                    "SERIALIZATION" -> SERILIZATION = value.trim()
                    "SOCKET_TIMEOUT" -> SOCKET_TIMEOUT = value.trim().toInt()
                    "PING_DELAY" -> PING_DELAY = value.trim().toInt()
                }
            }
        }
    }
}