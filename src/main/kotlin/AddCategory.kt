import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import data.TodoCategory
import org.jetbrains.exposed.sql.transactions.transaction

class AddCategory : CliktCommand("Add a todo category.") {
    val categoryName by argument()
    val isFavoured by option("--favoured").flag()

    override fun run() {
        transaction(db = database) {
            TodoCategory.new {
                name = categoryName
                favoured = isFavoured
            }
        }
    }
}