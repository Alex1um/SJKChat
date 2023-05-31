package Base.Messages

import java.io.Serializable

data class TextMessage(
    val message: String,
) : Serializable
