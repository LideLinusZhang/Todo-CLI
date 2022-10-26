import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import data.DataFactory
import data.TodoItem
import exceptions.IdNotFoundException
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.reflect.typeOf

class ModifyItem(private val dataFactory: DataFactory) : CliktCommand("Modify a todo item.") {
    private val itemId by argument(help = "Unique ID of the todo item.").int()
    private val field by option().choice("name", "description", "importance", "deadline", ignoreCase = true).required()
    private val value by argument(help = "To remove deadline, enter \"none\".")

    override fun run() {
        dataFactory.transaction {
            val item = TodoItem.findById(itemId)

            if (item === null)
                throw IdNotFoundException(itemId, typeOf<TodoItem>())

            when (field) {
                "name" -> item.name = value
                "description" -> item.description = value
                "importance" -> item.importance = enumValueOf(value)
                "deadline" -> {
                    if (value.lowercase() == "none")
                        item.deadline = null
                    else item.deadline = LocalDate.parse(value)
                }
            }

            item.modifiedTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }
}