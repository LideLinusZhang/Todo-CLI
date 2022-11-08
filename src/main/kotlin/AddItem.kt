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
import data.*
import edu.uwaterloo.cs.todo.lib.ItemImportance
import edu.uwaterloo.cs.todo.lib.TodoItemModel
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.and
import sync.SyncService
import java.util.*

class AddItem(private val dataFactory: DataFactory, private val syncService: SyncService) :
    CliktCommand(help = "Add a todo item to a pre-existing category.") {
    private val itemImportance by option("--importance").choice(ItemImportance.values().associateBy { it.name })
    private val itemDeadline by option("--deadline").convert { LocalDate.parse(it) }
    private val searchCategoryBy by option(help = "Type of identifiers used to determine which category to add to")
        .choice("id", "name").required()
    private val byUUID by option("--uuid", hidden = true).flag()

    private val categoryIdentifier by argument(help = "Value of the identifier used to determine which category to add to")
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

            if (!TodoItem.find { TodoItems.categoryId eq targetCategory.uniqueId and (TodoItems.name eq itemName) }
                    .empty())
                throw UsageError(text = "The Todo Item with the same name already exists under this category.")

            val model: TodoItemModel = TodoItem.new {
                name = itemName
                description = itemDescription ?: String()
                importance = itemImportance ?: ItemImportance.NORMAL
                deadline = itemDeadline
                categoryId = targetCategory.uniqueId
            }.toModel()

            runBlocking { syncService.addItem(targetCategory.uniqueId, model) }

            terminal.println("Item added successfully.")
        }
    }
}