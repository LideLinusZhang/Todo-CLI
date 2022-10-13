import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.groups.mutuallyExclusiveOptions
import com.github.ajalt.clikt.parameters.groups.required
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import edu.todo.lib.*

class AddItem : CliktCommand(help = "Add a todo item to a pre-existing category.") {
    val name by argument()
    val description by argument().optional()
    val category: TodoCategory by mutuallyExclusiveOptions(
        option("--category-unique-id").int().convert { categories.first { x -> x.UniqueId == it } },
        option("--category-name").convert { categories.first { x -> x.Name == it } }
    ).required()
    val importance by option("--importance").convert { enumValueOf<ItemImportance>(it) }

    override fun run() {
        items.add(TodoItem(name, description?:String(), category.UniqueId, importance?:ItemImportance.NORMAL, owner.UniqueId))
    }
}