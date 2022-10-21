import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import data.TodoCategory
import data.TodoItem
import data.TodoItems
import org.jetbrains.exposed.sql.transactions.transaction

class DeleteCategory : CliktCommand("Delete a todo category and all items under it.") {
    val categoryId by argument().int()

    override fun run() {
        transaction(db = database) {
            val category = TodoCategory.findById(categoryId)

            if (category === null)
                throw Exception() //TODO: Better error reporting

            val items = TodoItem.find { TodoItems.categoryId eq category.uniqueId }
            items.forEach { it.delete() }
            category.delete()
        }
    }
}