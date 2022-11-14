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
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.select
import sync.CloudService

class AddCategory(private val dataFactory: DataFactory, private val cloudService: CloudService?) :
    CliktCommand("Add a todo category.") {
    private val categoryName by argument(help = "Name of the category to be added.")
    private val isFavoured by option(
        "--favoured",
        help = "If entered, the added category will be set to be favoured."
    ).flag()
    private val terminal = Terminal()

    override fun run() {
        dataFactory.transaction {
            if (!TodoCategories.select { TodoCategories.name eq categoryName }.empty())
                throw PrintMessage("Category with the same name already exists.", error = true) // Name must be unique

            val newCategory = TodoCategory.new {
                name = categoryName
                favoured = isFavoured
            }

            val response = runBlocking { cloudService?.addCategory(newCategory.toModel()) }
            if (response !== null && !response.successful) {
                newCategory.delete()
                throw PrintMessage("Adding category failed: ${response.errorMessage}.", error = true)
            }

            terminal.println("Category added successfully.")
        }
    }
}