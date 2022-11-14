package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.terminal.Terminal
import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import exceptions.IdNotFoundException
import kotlinx.coroutines.runBlocking
import sync.CloudService
import java.util.*
import kotlin.reflect.typeOf

class DeleteCategory(private val dataFactory: DataFactory, private val cloudService: CloudService?) :
    CliktCommand("Delete a todo category and all items under it.") {
    private val byUUID by option("--uuid", hidden = true).flag(default = false)
    private val categoryId by argument(help = "ID of the category to be deleted")
    private val terminal = Terminal()

    override fun run() {
        dataFactory.transaction {
            val category = if (byUUID)
                TodoCategory.find { TodoCategories.uniqueId eq UUID.fromString(categoryId) }.firstOrNull()
            else TodoCategory.findById(categoryId.toInt())

            if (category === null)
                throw IdNotFoundException(categoryId.toInt(), typeOf<TodoCategory>())

            val response = runBlocking { cloudService?.deleteCategory(category.uniqueId) }
            if (response !== null && !response.successful) {
                throw PrintMessage("Deleting category failed: ${response.errorMessage}.", error = true)
            }

            category.delete()

            terminal.println("Category deleted successfully.")
        }
    }
}