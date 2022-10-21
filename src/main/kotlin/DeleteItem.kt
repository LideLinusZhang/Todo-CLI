import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import data.TodoItem

class DeleteItem : CliktCommand("Delete a todo item.") {
    val itemId by argument(help = "Unique ID of the todo item to delete.").int()

    override fun run() {
        factory.transaction {
            val items = TodoItem.findById(itemId)

            if (items === null)
                throw Exception() //TODO: Better error reporting

            items.delete()
        }
    }
}