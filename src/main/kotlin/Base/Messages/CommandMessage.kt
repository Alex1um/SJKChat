package Base.Messages

import java.io.Serializable

data class CommandMessage (
    val command: Command,
) : Serializable