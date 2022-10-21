import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import data.TodoCategory
import data.TodoItem
import data.TodoItems

class DeleteCategory : CliktCommand("Delete a todo category and all items under it.") {
    val categoryId by argument(help = "ID of the category to be deleted").int()

    override fun run() {
        factory.transaction {
            val category = TodoCategory.findById(categoryId)

            if (category === null)
                throw Exception() //TODO: Better error reporting

            val items = TodoItem.find { TodoItems.categoryId eq category.uniqueId }
            items.forEach { it.delete() }
            category.delete()
        }
    }
}