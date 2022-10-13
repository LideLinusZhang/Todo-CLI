import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import edu.todo.lib.TodoCategory

class AddCategory: CliktCommand("Add a todo category") {
    val name by argument()
    val description by argument().optional()

    override fun run() {
        categories.add(TodoCategory(name, description?: String(), owner.UniqueId))
    }
}