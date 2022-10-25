import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import data.DataFactory
import data.TodoItem
import exceptions.IdNotFoundException
import kotlin.reflect.typeOf

class DeleteItem(private val dataFactory: DataFactory) : CliktCommand("Delete a todo item.") {
    private val itemId by argument(help = "ID of the todo item to be deleted.").int()

    override fun run() {
        dataFactory.transaction {
            val items = TodoItem.findById(itemId)

            if (items === null)
                throw IdNotFoundException(itemId, typeOf<TodoItem>())

            items.delete()
        }
    }
}