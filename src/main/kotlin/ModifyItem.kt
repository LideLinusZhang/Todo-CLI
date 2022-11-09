import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.mordant.terminal.Terminal
import data.DataFactory
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.TodoItemModificationModel
import exceptions.IdNotFoundException
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import sync.SyncService
import kotlin.reflect.typeOf

class ModifyItem(private val dataFactory: DataFactory, private val syncService: SyncService) :
    CliktCommand("Modify a todo item.") {
    private val byUUID by option("--uuid", hidden = true).flag(default = false)
    private val itemId by argument(help = "ID of the todo item.")
    private val field by option(help = "Field to modify.").choice("name", "description", "favoured", "importance", "deadline", ignoreCase = true).required()
    private val value by argument(help = "To remove deadline, enter \"none\".")
    private val terminal = Terminal()

    override fun run() {
        dataFactory.transaction {
            val item = getItemById(byUUID, itemId)

            if (item === null)
                throw IdNotFoundException(itemId.toInt(), typeOf<TodoItem>())

            item.modifiedTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            val modificationModel: TodoItemModificationModel =
                when (field) {
                    "name" -> {
                        item.name = value
                        TodoItemModificationModel(item.name, null, null, null, null, item.modifiedTime)
                    }

                    "description" -> {
                        item.description = value
                        TodoItemModificationModel(null, item.description, null, null, null, item.modifiedTime)
                    }

                    "favoured" -> {
                        item.favoured = value.toBoolean()
                        TodoItemModificationModel(null, null, item.favoured, null, null, item.modifiedTime)
                    }

                    "importance" -> {
                        item.importance = enumValueOf(value)
                        TodoItemModificationModel(null, null, null, item.importance, null, item.modifiedTime)
                    }

                    "deadline" -> {
                        if (value.lowercase() == "none")
                            item.deadline = null
                        else item.deadline = LocalDate.parse(value)
                        TodoItemModificationModel(null, null, null, null, item.deadline, item.modifiedTime)
                    }

                    else -> TodoItemModificationModel(null, null, null, null, null, item.modifiedTime)
                }

            runBlocking { syncService.modifyItem(item.uniqueId, modificationModel) }

            terminal.println("Item modified successfully.")
        }
    }
}