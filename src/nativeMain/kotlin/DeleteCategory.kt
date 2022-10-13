import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int

class DeleteCategory : CliktCommand("Delete a todo category and all items under it.") {
    val id by argument().int()

    override fun run() {
        items.removeAll { it.CategoryId == id }
        categories.removeAll { it.UniqueId == id }
    }
}