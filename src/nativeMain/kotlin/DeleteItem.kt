import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert

class DeleteItem : CliktCommand("Delete a todo item.") {
    val id by argument(help = "Unique ID of the todo item to delete.").convert { it.toUInt() }

    override fun run() {
        items.removeAll { it.uniqueId == id }
    }
}