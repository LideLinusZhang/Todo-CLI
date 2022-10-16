import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.groups.mutuallyExclusiveOptions
import com.github.ajalt.clikt.parameters.groups.required
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import edu.todo.lib.*
import kotlinx.datetime.LocalDate

class AddItem : CliktCommand(help = "Add a todo item to a pre-existing category.") {
    val name by argument()
    val description by argument().optional()
    val category: TodoCategory by mutuallyExclusiveOptions(
        option("--category-unique-id").convert { categories.first { x -> x.uniqueId == it.toUInt() } },
        option("--category-name").convert { categories.first { x -> x.name == it } }
    ).required()
    val importance by option("--importance").convert { enumValueOf<ItemImportance>(it) }
    val deadline by option("--deadline").convert { LocalDate.parse(it) }

    override fun run() {
        items.add(TodoItem(name, description?:String(), category.uniqueId, importance?:ItemImportance.NORMAL, deadline ,owner.uniqueId))
    }
}