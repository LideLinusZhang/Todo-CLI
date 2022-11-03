import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.terminal.Terminal
import data.DataFactory
import data.TodoCategory
import edu.uwaterloo.cs.todo.lib.TodoCategoryModificationModel
import exceptions.IdNotFoundException
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import sync.SyncService
import kotlin.reflect.typeOf

class ModifyCategory(private val dataFactory: DataFactory, private val syncService: SyncService) :
    CliktCommand("Modify a todo category.") {
    private val categoryId by argument(help = "Unique ID of the todo category.").int()
    private val field by option().choice("name", "favoured", ignoreCase = true).required()
    private val value by argument()
    private val terminal = Terminal()

    override fun run() {
        dataFactory.transaction {
            val category = TodoCategory.findById(categoryId)

            if (category === null)
                throw IdNotFoundException(categoryId, typeOf<TodoCategory>())

            category.modifiedTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            val modificationModel: TodoCategoryModificationModel =
                when (field) {
                    "name" -> {
                        category.name = value
                        TodoCategoryModificationModel(category.name, null, category.modifiedTime)
                    }

                    "description" -> {
                        category.favoured = value.toBoolean()
                        TodoCategoryModificationModel(null, category.favoured, category.modifiedTime)
                    }

                    else -> TodoCategoryModificationModel(null, null, category.modifiedTime)
                }

            runBlocking { syncService.modifyCategory(category.uniqueId, modificationModel) }

            terminal.println("Category modified successfully.")
        }
    }
}