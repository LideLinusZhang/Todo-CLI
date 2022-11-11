package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.mordant.terminal.Terminal
import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.ItemImportance
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import sync.SyncService
import java.util.*

class AddItem(private val dataFactory: DataFactory, private val syncService: SyncService?) :
    CliktCommand(help = "Add a todo item to a pre-existing category.") {
    private val itemImportance by option("--importance")
        .choice(ItemImportance.values().associateBy { it.name })
    private val itemDeadline by option(
        "--deadline",
        help = "Deadline of the item, in the format of YYYY-MM-DD."
    )
        .convert { LocalDate.parse(it) }
    private val searchCategoryBy by option(
        help = "Type of identifiers used to determine which category to add to"
    )
        .choice("id", "name").required()
    private val isFavoured by option(
        "--favoured",
        help = "If entered, the added item will be set to be favoured."
    ).flag()
    private val byUUID by option("--uuid", hidden = true).flag()

    private val categoryIdentifier by argument(
        help = "Value of the identifier used to determine which category to add to"
    )
    private val itemName by argument("name")
    private val itemDescription by argument("description").optional()
    private val terminal = Terminal()

    override fun run() {
        dataFactory.transaction {
            val targetCategory: TodoCategory? = when (searchCategoryBy) {
                "id" -> if (byUUID)
                    TodoCategory.find { TodoCategories.uniqueId eq UUID.fromString(categoryIdentifier) }.firstOrNull()
                else
                    TodoCategory.findById(categoryIdentifier.toInt())

                "name" -> TodoCategory.find { TodoCategories.name eq categoryIdentifier }.firstOrNull()
                else -> null
            }

            if (targetCategory === null)
                throw UsageError(text = "The target category doesn't exist.")

            if (targetCategory.items.any { it.name == itemName })
                throw UsageError(text = "An item with the same name already exists under the given category.")

            val newItem = TodoItem.new {
                name = itemName
                description = itemDescription ?: String()
                favoured = isFavoured
                importance = itemImportance ?: ItemImportance.NORMAL
                deadline = itemDeadline
                categoryId = targetCategory.uniqueId
            }

            val response = runBlocking { syncService?.addItem(targetCategory.uniqueId, newItem.toModel()) }
            if (response !== null && !response.successful) {
                newItem.delete()
                throw UsageError("Adding item failed: ${response.errorMessage}.")
            }

            terminal.println("Item added successfully.")
        }
    }
}