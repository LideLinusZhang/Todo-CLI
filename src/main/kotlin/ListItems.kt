import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import data.TodoCategory
import data.TodoItem
import data.TodoItems

//List all todo items under a category.
class ListItems : CliktCommand("List all todo items under a category.") {
    val categoryId by argument().int()

    override fun run() {
        val t = Terminal()

        t.println(table {
            tableBorders = Borders.NONE

            header {
                style(bold = true)
                row("Unique ID", "Name", "Description", "Importance", "Deadline")
            }
            body {
                cellBorders = Borders.LEFT_RIGHT
                factory.transaction {
                    val category = TodoCategory.findById(categoryId)

                    if (category === null)
                        throw Exception() // TODO: Better error message

                    val items = TodoItem.find { TodoItems.categoryId eq category.uniqueId }

                    items.forEach {
                        row {
                            cell(it.uniqueId)
                            cell(it.name)
                            cell(it.description)
                            cell(it.importance)
                            cell(it.deadline)
                        }
                    }
                }
            }
            column(0) {
                width = ColumnWidth.Fixed(14)
            }
            column(1) {
                width = ColumnWidth.Fixed(20)
            }
            column(2) {
                width = ColumnWidth.Fixed(20)
            }
            column(3) {
                width = ColumnWidth.Fixed(15)
            }
            column(4) {
                width = ColumnWidth.Fixed(15)
            }
        })
    }
}