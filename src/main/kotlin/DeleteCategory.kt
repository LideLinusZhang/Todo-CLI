import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import org.jetbrains.exposed.sql.transactions.transaction
import data.*

class DeleteCategory : CliktCommand("Delete a todo category and all items under it.") {
    val id by argument().convert{ it.toUInt() }

    override fun run() {
        transaction(db = database) {
            items.removeAll { it.categoryId == id }
            categories.removeAll { it.uniqueId == id }
        }

    }
}