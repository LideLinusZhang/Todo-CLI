package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.mordant.terminal.Terminal
import data.DataFactory
import data.TodoCategory
import data.TodoCategoryFields
import edu.uwaterloo.cs.todo.lib.TodoCategoryModificationModel
import exceptions.IdNotFoundException
import getCategoryById
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import sync.CloudService
import kotlin.reflect.typeOf

class ModifyCategory(private val dataFactory: DataFactory, private val cloudService: CloudService?) :
    CliktCommand("Modify a todo category.") {
    private val byUUID by option("--uuid", hidden = true).flag(default = false)
    private val categoryId by argument(help = "ID of the todo category.")
    private val field by option().choice(TodoCategoryFields.values().dropWhile { it == TodoCategoryFields.Id }
        .associateBy { it.name }, ignoreCase = true).required()
    private val value by argument()
    private val terminal = Terminal()

    override fun run() {
        dataFactory.transaction {
            val category = getCategoryById(byUUID, categoryId)

            if (category === null)
                throw IdNotFoundException(categoryId, typeOf<TodoCategory>())

            category.modifiedTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val modification: () -> Unit

            val modificationModel: TodoCategoryModificationModel =
                when (field) {
                    TodoCategoryFields.Name -> {
                        modification = { category.name = value }
                        TodoCategoryModificationModel(value, null, category.modifiedTime)
                    }

                    TodoCategoryFields.Favoured -> {
                        val modifiedTo = value.toBoolean()
                        modification = { category.favoured = modifiedTo }
                        TodoCategoryModificationModel(null, category.favoured, category.modifiedTime)
                    }

                    else -> {
                        modification = {}
                        TodoCategoryModificationModel(null, null, category.modifiedTime)
                    }
                }

            val response = runBlocking { cloudService?.modifyCategory(category.uniqueId, modificationModel) }
            if (response !== null && !response.successful)
                throw PrintMessage("Modifying category failed: ${response.errorMessage}.", error = true)

            modification.invoke()

            terminal.println("Category modified successfully.")
        }
    }
}