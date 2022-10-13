import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int

class DeleteItem : CliktCommand("Delete a todo item.") {
    val id by argument(help = "Unique ID of the todo item to delete.").int()

    override fun run() {
        items.removeAll { it.Id == id }
    }
}