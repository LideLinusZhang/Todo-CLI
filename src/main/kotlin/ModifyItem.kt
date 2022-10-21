import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import data.TodoItem
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.transactions.transaction

class ModifyItem : CliktCommand("Modify a todo item.") {
    val itemId by argument(help ="Unique ID of the todo item.").int()
    val field by option().choice("name", "description", "importance", "deadline", ignoreCase = true).required()
    val value by argument(help = "To remove deadline, enter \"none\".")

    override fun run() {
        transaction(db = database) {
            val item = TodoItem.findById(itemId)

            if(item === null)
                throw Exception() //TODO: Better error reporting

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
        }
    }
}