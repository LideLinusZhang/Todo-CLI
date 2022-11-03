import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.terminal.Terminal
import data.DataFactory
import data.TodoItem
import exceptions.IdNotFoundException
import kotlinx.coroutines.runBlocking
import sync.SyncService
import kotlin.reflect.typeOf

class DeleteItem(private val dataFactory: DataFactory, private val syncService: SyncService) : CliktCommand("Delete a todo item.") {
    private val itemId by argument(help = "ID of the todo item to be deleted.").int()
    private val terminal = Terminal()

    override fun run() {
        dataFactory.transaction {
            val item = TodoItem.findById(itemId)

            if (item === null)
                throw IdNotFoundException(itemId, typeOf<TodoItem>())

            runBlocking { syncService.deleteItem(item.uniqueId) }
            item.delete()

            terminal.println("Item deleted successfully.")
        }
    }
}