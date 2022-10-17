import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice

class ModifyCategory: CliktCommand("Modify a todo category.") {
    val category by argument("ID", "Unique ID of the todo category.").convert { categories.first { x -> x.uniqueId == it.toUInt() } }
    val field by option().choice("name","favoured").required()
    val value by argument()

    override fun run() {
        when (field)
        {
            "name" -> category.name = value
            "description" -> category.favored = value.toBoolean()
        }
    }
}