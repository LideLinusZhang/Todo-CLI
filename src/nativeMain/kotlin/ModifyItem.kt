import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import kotlinx.datetime.LocalDate

class ModifyItem : CliktCommand("Modify a todo item.") {
    val item by argument("ID", "Unique ID of the todo item.").convert {
        items.first { x -> x.uniqueId == it.toUInt() }
    }
    val field by option().choice("name", "description", "importance", "deadline", ignoreCase = true).required()
    val value by argument(help = "To remove deadline, enter \"none\".")

    override fun run() {
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