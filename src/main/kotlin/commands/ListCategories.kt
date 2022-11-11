package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import data.DataFactory
import data.TodoCategory
import edu.uwaterloo.cs.todo.lib.TodoCategoryModel
import edu.uwaterloo.cs.todo.lib.serializeCategoryList
import org.jetbrains.exposed.sql.SizedIterable

class ListCategories(private val dataFactory: DataFactory) : CliktCommand("Display todo categories.") {
    private val outputJSON by option("--json", hidden = true).flag(default = false)
    private val terminal = Terminal()

    private fun outputJSON(categories: List<TodoCategoryModel>) {
        terminal.print(serializeCategoryList(categories))
    }

    private fun outputTable(categories: SizedIterable<TodoCategory>) {
        if (categories.empty()) {
            terminal.println("There is no category.")
            return
        }

        terminal.println(table {
            tableBorders = Borders.NONE
            header { style(bold = true); row("ID", "Name", "Favoured?") }
            body {
                cellBorders = Borders.LEFT_RIGHT
                categories.forEach {
                    row {
                        cell(it.id); cell(it.name)
                        cell(if (it.favoured) "Yes" else "No") {
                            align = TextAlign.CENTER
                            if (it.favoured)
                                style(color = TextColors.brightGreen, bold = true)
                            else
                                style(color = TextColors.brightRed, bold = true)
                        }
                    }
                }
            }
            column(0) { width = ColumnWidth.Fixed(10) }
            column(1) { width = ColumnWidth.Fixed(50) }
            column(2) { width = ColumnWidth.Fixed(15) }
        })
    }

    override fun run() {
        dataFactory.transaction {
            val categories = TodoCategory.all().notForUpdate()
            if (outputJSON)
                outputJSON(categories.map { it.toModel() })
            else
                outputTable(categories)
        }
    }
}
