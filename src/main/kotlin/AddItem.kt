import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.ItemImportance
import kotlinx.datetime.LocalDate

class AddItem(private val dataFactory: DataFactory) : CliktCommand(help = "Add a todo item to a pre-existing category.") {
    private val itemImportance by option("--importance").choice(ItemImportance.values().associateBy { it.name })
    private val itemDeadline by option("--deadline").convert { LocalDate.parse(it) }
    private val searchCategoryBy by option(help = "Type of identifiers used to determine which category to add to")
        .choice("id", "name").required()

    private val categoryIdentifier by argument()
    private val itemName by argument("name")
    private val itemDescription by argument("description").optional()

    override fun run() {
        dataFactory.transaction {
            val targetCategory: TodoCategory? = when (searchCategoryBy) {
                "id" -> {
                    val targetCategoryId = categoryIdentifier.toInt()
                    TodoCategory.findById(targetCategoryId)
                }

                "name" -> TodoCategory.find { TodoCategories.name eq categoryIdentifier }.firstOrNull()
                else -> null
            }

            if (targetCategory === null)
                throw Exception() //TODO: Better error reporting

            TodoItem.new {
                name = itemName
                description = itemDescription ?: String()
                importance = itemImportance ?: ItemImportance.NORMAL
                deadline = itemDeadline
                categoryId = targetCategory.uniqueId
            }
        }
    }
}