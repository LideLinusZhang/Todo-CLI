import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import data.*
import edu.uwaterloo.cs.todo.lib.TodoItemModel
import edu.uwaterloo.cs.todo.lib.serializeItemList
import exceptions.IdNotFoundException
import org.jetbrains.exposed.sql.SizedIterable
import kotlin.reflect.typeOf

//List all todo items under a category.
class ListItems(private val dataFactory: DataFactory) : CliktCommand("List all todo items under a category.") {
    private val outputJSON by option("--json", hidden = true).flag(default = false)
    private val byUUID by option("--uuid", hidden = true).flag(default = false)
    private val categoryId by argument(help = "ID of the todo category.")
    private val terminal = Terminal()

    private fun outputJSON(items: List<TodoItemModel>) {
        terminal.print(serializeItemList(items))
    }

    private fun outputTable(items: SizedIterable<TodoItem>) {
        if (items.empty()) {
            terminal.println("There is no item.")
            return
        }

        terminal.println(table {
            tableBorders = Borders.NONE
            header {
                style(bold = true)
                row("ID", "Name", "Description", "Importance", "Deadline")
            }
            body {
                cellBorders = Borders.LEFT_RIGHT
                items.forEach {
                    row {
                        cell(it.id); cell(it.name); cell(it.description)
                        cell(it.importance); cell(it.deadline ?: "N/A")
                    }
                }
            }
            column(0) { width = ColumnWidth.Fixed(14) }
            column(1) { width = ColumnWidth.Fixed(20) }
            column(2) { width = ColumnWidth.Fixed(20) }
            column(3) { width = ColumnWidth.Fixed(15) }
            column(4) { width = ColumnWidth.Fixed(15) }
        })
    }

    override fun run() {
        dataFactory.transaction {
            val category = getCategoryById(byUUID, categoryId)

            if (category === null)
                throw IdNotFoundException(categoryId.toInt(), typeOf<TodoCategory>())
            val items = TodoItem.find { TodoItems.categoryId eq category.uniqueId }

            if (outputJSON)
                outputJSON(items.map { it.toModel() })
            else
                outputTable(items)
        }
    }
}