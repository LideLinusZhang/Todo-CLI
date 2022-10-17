import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import edu.todo.lib.TodoCategory

class AddCategory: CliktCommand("Add a todo category.") {
    val name by argument()
    val favoured by option("--favoured").flag()

    override fun run() {
        categories.add(TodoCategory(name, favoured, owner.uniqueId))
    }
}