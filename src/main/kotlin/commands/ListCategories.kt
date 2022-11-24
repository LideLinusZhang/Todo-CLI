package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import data.TodoCategoryFields
import edu.uwaterloo.cs.todo.lib.TodoCategoryModel
import edu.uwaterloo.cs.todo.lib.serializeCategoryList
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SortOrder

class ListCategories(private val dataFactory: DataFactory) : CliktCommand("Display todo categories.") {
    private val outputJSON by option("--json", hidden = true).flag(default = false)
    private val sortedBy by option().choice(TodoCategoryFields.values().associateBy { it.name }, ignoreCase = true)
        .default(TodoCategoryFields.Id)
    private val order by option("--descending").flag()
        .convert { if (it) SortOrder.ASC_NULLS_LAST else SortOrder.DESC_NULLS_FIRST }
    private val terminal = Terminal()

    private fun outputJSON(categories: List<TodoCategoryModel>) {
        terminal.print(serializeCategoryList(categories))
    }

    private fun outputTable(categories: SizedIterable<TodoCategory>) {
        if (categories.empty())
            throw PrintMessage("There is no category.", error = false)

        terminal.println(table {
            tableBorders = Borders.NONE
            header { style(bold = true); row("ID", "Name", "Favr?"); align = TextAlign.CENTER }
            body {
                cellBorders = Borders.LEFT_RIGHT
                categories.forEach {
                    row {
                        cell(it.id)
                        cell(it.name)
                        cell(if (it.favoured) "Yes" else "No") {
                            val color = if (it.favoured) TextColors.brightGreen else TextColors.brightRed
                            style(color = color, bold = true)
                        }
                    }
                }
            }
            column(0) { width = ColumnWidth.Fixed(4); align = TextAlign.CENTER }
            column(1) { width = ColumnWidth.Expand() }
            column(2) { width = ColumnWidth.Fixed(7); align = TextAlign.CENTER }
        })
    }

    override fun run() {
        dataFactory.transaction {
            val categories = when (sortedBy) {
                TodoCategoryFields.Id -> TodoCategory.all().notForUpdate()
                    .orderBy(TodoCategories.id to order)

                TodoCategoryFields.Favoured -> TodoCategory.all().notForUpdate()
                    .orderBy(TodoCategories.favoured to order)

                TodoCategoryFields.Name -> TodoCategory.all().notForUpdate()
                    .orderBy(TodoCategories.name to order)
            }

            if (outputJSON)
                outputJSON(categories.map { it.toModel() })
            else
                outputTable(categories)
        }
    }
}
