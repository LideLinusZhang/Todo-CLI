import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import data.DataFactory
import data.TodoCategory
import data.TodoItem
import data.TodoItems
import exceptions.IdNotFoundException
import kotlin.reflect.typeOf

class DeleteCategory(private val dataFactory: DataFactory) : CliktCommand("Delete a todo category and all items under it.") {
    private val categoryId by argument(help = "ID of the category to be deleted").int()

    override fun run() {
        dataFactory.transaction {
            val category = TodoCategory.findById(categoryId)

            if (category === null)
                throw IdNotFoundException(categoryId, typeOf<TodoCategory>())

            val items = TodoItem.find { TodoItems.categoryId eq category.uniqueId }
            items.forEach { it.delete() }
            category.delete()
        }
    }
}