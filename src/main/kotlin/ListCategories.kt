import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import data.TodoCategory
import org.jetbrains.exposed.sql.transactions.transaction

class ListCategories : CliktCommand("Display todo categories.") {
    override fun run() {
        val t = Terminal()

        t.println(table {
            tableBorders = Borders.NONE

            header {
                style(bold = true)
                row("Unique ID", "Name", "Favoured?")
            }
            body {
                cellBorders = Borders.LEFT_RIGHT
                transaction(db = database) {
                    val categories = TodoCategory.all().notForUpdate()

                    if(categories.empty())
                        throw Exception() //TODO: Better error reporting

                    categories.forEach {
                        row {
                            cell(it.id)
                            cell(it.name)
                            cell(if(it.favoured) "Yes" else "No") {
                                if (it.favoured)
                                    style(color = TextColors.brightGreen, bold = true)
                                else
                                    style(color = TextColors.brightRed, bold = true)
                            }
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