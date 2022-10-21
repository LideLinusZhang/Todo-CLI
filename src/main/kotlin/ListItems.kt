import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import edu.todo.lib.ItemImportance
import edu.todo.lib.TodoItem
import kotlinx.datetime.LocalDate

//List all todo items under a category.
class ListItems : CliktCommand("List all todo items under a category.") {
    val categoryId by argument().convert { it.toUInt() }

    override fun run() {
        val t = Terminal()

        val date = LocalDate(2016, 3, 15)


        items.add(TodoItem("241A1", "mips", 9U, ItemImportance.LOW, date,1U))
        items.add(TodoItem("240A5", "data structure", 8U, ItemImportance.NORMAL, date,2U))
        items.add(TodoItem("246A4", "oop", 8U, ItemImportance.HIGH, date,3U))

        t.println(table {
            tableBorders = Borders.NONE

            header {
                style(bold = true)
                row("Unique ID", "Name", "description","categoryId", "importance","deadline")
            }
            body {
                cellBorders = Borders.LEFT_RIGHT
                items.filter { it.categoryId == categoryId }
                    .forEach {
                    row {
                        cell(it.uniqueId)
                        cell(it.name)
                        cell(it.description)
                        cell(it.categoryId)
                        cell(it.importance)
                        cell(it.deadline)
                    }
                }
            }
            column(0) {
                width = ColumnWidth.Fixed(14)
            }
            column(1) {
                width = ColumnWidth.Fixed(50)
            }
            column(2) {
                width = ColumnWidth.Fixed(50)
            }
            column(3) {
                width = ColumnWidth.Fixed(15)
            }
            column(4) {
                width = ColumnWidth.Fixed(15)
            }
            column(5) {
                width = ColumnWidth.Fixed(15)
            }
        })
    }
}