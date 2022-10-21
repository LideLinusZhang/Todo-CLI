import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.*
import com.github.ajalt.mordant.terminal.Terminal
import edu.todo.lib.TodoCategory



//Display all todo categories
class ListCategories : CliktCommand("Display todo categories.") {
    override fun run() {
        val t = Terminal()

        categories.add(TodoCategory("Homework", true, 1u))
        categories.add(TodoCategory("shopping", false, 2u))

        t.println(table {
            tableBorders = Borders.NONE

            header {
                style(bold = true)
                row("Unique ID", "Category Name", "Favoured?")
            }
            body {
                cellBorders = Borders.LEFT_RIGHT
                categories.forEach {
                    row {
                        cell(it.uniqueId)
                        cell(it.name)
                        cell(it.favored) {
                            if(it.favored)
                                style(color = TextColors.brightRed, bold = true)
                            else
                                style(color = TextColors.blue, italic = true)
                        }
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
                width = ColumnWidth.Fixed(15)
            }
        })
    }
}