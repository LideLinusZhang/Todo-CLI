import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import org.jetbrains.exposed.sql.select

class AddCategory(private val dataFactory: DataFactory) : CliktCommand("Add a todo category.") {
    private val categoryName by argument(help = "Name of the category to be added.")
    private val isFavoured by option("--favoured", help = "If entered, the added category will be set to be favoured.").flag()

    override fun run() {
        dataFactory.transaction {
            if (!TodoCategories.select { TodoCategories.name eq categoryName }.empty())
                throw Exception() // Name must be unique, TODO: Better error reporting

            TodoCategory.new {
                name = categoryName
                favoured = isFavoured
            }
        }
    }
}