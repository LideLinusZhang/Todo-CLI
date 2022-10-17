import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.colormath.Color
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.buildWidget
import com.github.ajalt.mordant.table.*
import com.github.ajalt.mordant.terminal.Terminal
import edu.todo.lib.TodoCategory
import platform.windows.szOID_RSA_envelopedData

class ListCategories : CliktCommand("Display todo categories.") {
    override fun run() {
        val t = Terminal()

        categories.add(TodoCategory("T", true, 1u))

        t.println(table {
            tableBorders = Borders.NONE

            header {
                style(bold = true)
                row("Unique ID", "Name", "Favoured?")
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
                        }
                    }
                }
            }
            column(0) {
                width = ColumnWidth.Fixed(12)
            }
            column(1) {
                width = ColumnWidth.Fixed(50)
            }
            column(2) {
                width = ColumnWidth.Fixed(10)
            }
        })
    }
}