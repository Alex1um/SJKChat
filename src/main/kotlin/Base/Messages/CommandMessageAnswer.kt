package Base.Messages

import java.io.Serializable

data class CommandMessageAnswer (
    val command: Command,
    val data: String
): Serializable