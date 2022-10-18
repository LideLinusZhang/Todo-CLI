import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert

class ListItems : CliktCommand("List all todo items under a category.") {
    val categoryId by argument().convert { it.toUInt() }

    override fun run() {

    }
}