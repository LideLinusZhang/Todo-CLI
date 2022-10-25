import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import data.DataFactory
import data.TodoCategory
import exceptions.IdNotFoundException
import kotlin.reflect.typeOf

class ModifyCategory(private val dataFactory: DataFactory) : CliktCommand("Modify a todo category.") {
    private val categoryId by argument(help = "Unique ID of the todo category.").int()
    private val field by option().choice("name", "favoured", ignoreCase = true).required()
    private val value by argument()

    override fun run() {
        dataFactory.transaction {
            val category = TodoCategory.findById(categoryId)

            if (category === null)
                throw IdNotFoundException(categoryId, typeOf<TodoCategory>())

            when (field) {
                "name" -> category.name = value
                "description" -> category.favoured = value.toBoolean()
            }
        }
    }
}