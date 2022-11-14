package exceptions

import com.github.ajalt.clikt.core.PrintMessage
import kotlin.reflect.KType

class IdNotFoundException(id: Int, type: KType) : PrintMessage(
    message = "The ${type.toString().split('.').last()} ID $id cannot be found.",
    error = true
)