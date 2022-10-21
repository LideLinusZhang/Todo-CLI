import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import data.TodoCategory

class AddCategory : CliktCommand("Add a todo category.") {
    val categoryName by argument()
    val isFavoured by option("--favoured").flag()

    override fun run() {
        factory.transaction {
            TodoCategory.new {
                name = categoryName
                favoured = isFavoured
            }
        }
    }
}