import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.table.*
import com.github.ajalt.mordant.terminal.Terminal

class ListCategories : CliktCommand("Display todo categories.") {
    override fun run() {
        val t = Terminal()

        t.println(table {
            tableBorders = Borders.NONE

            header {
                style(bold = true)
                row("Unique ID", "Name", "Description")
            }
            body {
                cellBorders = Borders.LEFT_RIGHT
                categories.forEach {
                    row(  it.name,it.uniqueId,it.description )
                }
            }
            column(0) {
                width = ColumnWidth.Fixed(12)
            }
            column(1) {
                width = ColumnWidth.Fixed(20)
            }
            column(2) {
                width = ColumnWidth.Fixed(50)
            }
        })
    }
}