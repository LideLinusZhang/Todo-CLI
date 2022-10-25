package exceptions

import com.github.ajalt.clikt.core.UsageError
import kotlin.reflect.KType

class IdNotFoundException(id: Int, type: KType) : UsageError(
    text = "The ${type.toString().split('.').last()} ID $id cannot be found."
)