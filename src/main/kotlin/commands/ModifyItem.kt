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
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.ItemImportance
import edu.uwaterloo.cs.todo.lib.TodoItemModificationModel
import exceptions.IdNotFoundException
import getItemById
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import sync.CloudService
import kotlin.reflect.typeOf

private const val deadlineRemover: String = "none"

class ModifyItem(private val dataFactory: DataFactory, private val cloudService: CloudService?) :
    CliktCommand("Modify a todo item.") {
    private val byUUID by option("--uuid", hidden = true).flag(default = false)
    private val itemId by argument(help = "ID of the todo item.")
    private val field by option(help = "Field to modify.").choice(
        "name",
        "description",
        "favoured",
        "importance",
        "deadline",
        ignoreCase = true
    ).required()
    private val value by argument(
        help = "Value that the field will be modified to be.\u0085" +
                "For favoured, it should be either true or false.\u0085" +
                "For importance, it should be in ${ItemImportance.values().map { it.name }}.\u0085"+
                "For deadline, the value should be in the format of YYYY-MM-DD. " +
                "To remove deadline, enter \"$deadlineRemover\"."
    )
    private val terminal = Terminal()

    override fun run() {
        dataFactory.transaction {
            val item = getItemById(byUUID, itemId)

            if (item === null)
                throw IdNotFoundException(itemId.toInt(), typeOf<TodoItem>())

            item.modifiedTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            val modification: () -> Unit

            val modificationModel: TodoItemModificationModel =
                when (field) {
                    "name" -> {
                        modification = { item.name = value }
                        TodoItemModificationModel(
                            name = item.name,
                            description = null,
                            favoured = null,
                            importance = null,
                            deadline = null,
                            modifiedTime = item.modifiedTime
                        )
                    }

                    "description" -> {
                        modification = { item.description = value }
                        TodoItemModificationModel(
                            name = null,
                            description = item.description,
                            favoured = null,
                            importance = null,
                            deadline = null,
                            modifiedTime = item.modifiedTime
                        )
                    }

                    "favoured" -> {
                        modification = { item.favoured = value.toBoolean() }
                        TodoItemModificationModel(
                            name = null,
                            description = null,
                            favoured = item.favoured,
                            importance = null,
                            deadline = null,
                            modifiedTime = item.modifiedTime
                        )
                    }

                    "importance" -> {
                        modification = { item.importance = enumValueOf(value) }
                        TodoItemModificationModel(
                            name = null,
                            description = null,
                            favoured = null,
                            importance = item.importance,
                            deadline = null,
                            modifiedTime = item.modifiedTime
                        )
                    }

                    "deadline" -> {
                        modification = if (value.lowercase() == deadlineRemover) {
                            { item.deadline = null }
                        } else {
                            { item.deadline = LocalDate.parse(value) }
                        }
                        TodoItemModificationModel(
                            name = null,
                            description = null,
                            favoured = null,
                            importance = null,
                            deadline = item.deadline,
                            modifiedTime = item.modifiedTime
                        )
                    }

                    else -> {
                        modification = {}
                        TodoItemModificationModel(
                            name = null,
                            description = null,
                            favoured = null,
                            importance = null,
                            deadline = null,
                            modifiedTime = item.modifiedTime
                        )
                    }
                }

            val response = runBlocking { cloudService?.modifyItem(item.uniqueId, modificationModel) }
            if (response !== null && !response.successful)
                throw PrintMessage("Modifying item failed: ${response.errorMessage}.", error = true)

            modification.invoke()

            terminal.println("Item modified successfully.")
        }
    }
}