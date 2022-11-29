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
import data.TodoItemFields
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
        TodoItemFields.values().dropWhile { it == TodoItemFields.Id }.associateBy { it.name },
        ignoreCase = true
    ).required()
    private val value by argument(
        help = "Value that the field will be modified to be.\u0085" +
                "For favoured, it should be either true or false.\u0085" +
                "For importance, it should be in ${ItemImportance.values().map { it.name }}.\u0085" +
                "For deadline, the value should be in the format of YYYY-MM-DD. " +
                "To remove deadline, enter \"$deadlineRemover\"."
    )
    private val terminal = Terminal()

    override fun run() {
        dataFactory.transaction {
            val item = getItemById(byUUID, itemId)

            if (item === null)
                throw IdNotFoundException(itemId, typeOf<TodoItem>())

            item.modifiedTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            val modification: () -> Unit

            val modificationModel: TodoItemModificationModel =
                when (field) {
                    TodoItemFields.Name -> {
                        modification = { item.name = value }
                        TodoItemModificationModel(
                            name = value,
                            modifiedTime = item.modifiedTime
                        )
                    }

                    TodoItemFields.Description -> {
                        modification = { item.description = value }
                        TodoItemModificationModel(
                            description = value,
                            modifiedTime = item.modifiedTime
                        )
                    }

                    TodoItemFields.Favoured -> {
                        val modifiedTo = value.toBoolean()
                        modification = { item.favoured = modifiedTo }
                        TodoItemModificationModel(
                            favoured = modifiedTo,
                            modifiedTime = item.modifiedTime
                        )
                    }

                    TodoItemFields.Importance -> {
                        val modifiedTo = enumValueOf<ItemImportance>(value)
                        modification = { item.importance = modifiedTo }
                        TodoItemModificationModel(
                            importance = modifiedTo,
                            modifiedTime = item.modifiedTime
                        )
                    }

                    TodoItemFields.Deadline -> {
                        val modifiedTo = if (value.lowercase() == deadlineRemover) null else LocalDate.parse(value)
                        modification = { item.deadline = modifiedTo }
                        TodoItemModificationModel(
                            deadline = modifiedTo,
                            modifiedTime = item.modifiedTime
                        )
                    }

                    else -> {
                        modification = {}
                        TodoItemModificationModel(
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