package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.terminal.Terminal
import data.DataFactory
import data.TodoItem
import exceptions.IdNotFoundException
import getItemById
import kotlinx.coroutines.runBlocking
import sync.SyncService
import kotlin.reflect.typeOf

class DeleteItem(private val dataFactory: DataFactory, private val syncService: SyncService?) :
    CliktCommand("Delete a todo item.") {
    private val byUUID by option("--uuid", hidden = true).flag(default = false)
    private val itemId by argument(help = "ID of the todo item to be deleted.")
    private val terminal = Terminal()

    override fun run() {
        dataFactory.transaction {
            val item = getItemById(byUUID, itemId)

            if (item === null)
                throw IdNotFoundException(itemId.toInt(), typeOf<TodoItem>())

            val response = runBlocking { syncService?.deleteItem(item.uniqueId) }
            if (response !== null && !response.successful) {
                throw UsageError("Deleting item failed: ${response.errorMessage}.")
            }

            item.delete()

            terminal.println("Item deleted successfully.")
        }
    }
}