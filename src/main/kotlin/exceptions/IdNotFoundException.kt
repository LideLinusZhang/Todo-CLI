package exceptions

import com.github.ajalt.clikt.core.PrintMessage
import kotlin.reflect.KType

class IdNotFoundException(id: String, type: KType) : PrintMessage(
    message = "${type.toString().split('.').last().removePrefix("Todo")} with ID $id does not exist.",
    error = true
)