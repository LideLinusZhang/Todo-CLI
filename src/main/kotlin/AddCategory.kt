import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import data.TodoCategory

class AddCategory : CliktCommand("Add a todo category.") {
    val categoryName by argument(help = "Name of the category to be added.")
    val isFavoured by option("--favoured", help = "If entered, the added category will be set to be favoured.").flag()

    override fun run() {
        factory.transaction {
            TodoCategory.new {
                name = categoryName
                favoured = isFavoured
            }
        }
    }
}