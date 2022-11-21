package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
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
import sync.CloudService
import java.util.*

class AddItem(private val dataFactory: DataFactory, private val cloudService: CloudService?) :
    CliktCommand(help = "Add a todo item to a pre-existing category.") {
    private val itemImportance by option("--importance")
        .choice(ItemImportance.values().associateBy { it.name })
    private val itemDeadline by option(
        "--deadline",
        metavar = "YYYY-MM-DD",
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
                throw PrintMessage("The target category does not exist.", error = true)

            if (targetCategory.items.any { it.name == itemName })
                throw PrintMessage("An item with the same name already exists under the given category.", error = true)

            val newItem = TodoItem.new {
                name = itemName
                description = itemDescription ?: String()
                favoured = isFavoured
                importance = itemImportance ?: ItemImportance.NORMAL
                deadline = itemDeadline
                categoryId = targetCategory.uniqueId
            }

            val response = runBlocking { cloudService?.addItem(newItem.toModel()) }
            if (response !== null && !response.successful) {
                newItem.delete()
                throw PrintMessage("Adding item failed: ${response.errorMessage}.", error = true)
            }

            terminal.println("Item added successfully.")
        }
    }
}