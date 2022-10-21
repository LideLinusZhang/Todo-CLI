import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import data.TodoCategory
import org.jetbrains.exposed.sql.transactions.transaction

class ModifyCategory : CliktCommand("Modify a todo category.") {
    val categoryId by argument(help = "Unique ID of the todo category.").int()
    val field by option().choice("name", "favoured", ignoreCase = true).required()
    val value by argument()

    override fun run() {
        transaction(db = database) {
            val category = TodoCategory.findById(categoryId)

            if (category === null)
                throw Exception() //TODO: Better error reporting

            when (field) {
                "name" -> category.name = value
                "description" -> category.favoured = value.toBoolean()
            }
        }
    }
}