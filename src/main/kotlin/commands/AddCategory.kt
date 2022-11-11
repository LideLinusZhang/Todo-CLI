package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.terminal.Terminal
import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import edu.uwaterloo.cs.todo.lib.TodoCategoryModel
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.select
import sync.SyncService

class AddCategory(private val dataFactory: DataFactory, private val syncService: SyncService?) :
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
                throw UsageError(text = "Category with the same name already exists.") // Name must be unique

            val model: TodoCategoryModel = TodoCategory.new {
                name = categoryName
                favoured = isFavoured
            }.toModel()

            runBlocking { syncService?.addCategory(model) }

            terminal.println("Category added successfully.")
        }
    }
}