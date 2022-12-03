package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.arguments.argument
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
import data.*
import edu.uwaterloo.cs.todo.lib.ItemImportance
import edu.uwaterloo.cs.todo.lib.ItemImportance.*
import edu.uwaterloo.cs.todo.lib.TodoItemModel
import edu.uwaterloo.cs.todo.lib.serializeItemList
import exceptions.IdNotFoundException
import getCategoryById
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SortOrder
import kotlin.reflect.typeOf

//List all to-do items under a category.
class ListItems(private val dataFactory: DataFactory) :
    CliktCommand("List all todo items under a category.") {
    private val outputJSON by option("--json", hidden = true).flag(default = false)
    private val byUUID by option("--uuid", hidden = true).flag(default = false)
    private val sortedBy by option().choice(TodoItemFields.values().associateBy { it.name }, ignoreCase = true)
        .default(TodoItemFields.Id)
    private val order by option("--descending").flag()
        .convert { if (it) SortOrder.ASC_NULLS_LAST else SortOrder.DESC_NULLS_FIRST }
    private val categoryId by argument(help = "ID of the todo category.")
    private val terminal = Terminal()

    companion object {
        private fun getImportanceColor(importance: ItemImportance): TextColors {
            return when (importance) {
                CRITICAL -> TextColors.brightRed
                VERY_HIGH -> TextColors.brightMagenta
                HIGH -> TextColors.brightYellow
                NORMAL -> TextColors.brightWhite
                BELOW_NORMAL -> TextColors.brightCyan
                LOW -> TextColors.brightBlue
            }
        }
    }

    private fun outputJSON(items: List<TodoItemModel>) {
        terminal.print(serializeItemList(items))
    }

    private fun outputTable(items: SizedIterable<TodoItem>) {
        if (items.empty())
            throw PrintMessage("There is no item under the given category.", error = true)

        terminal.println(table {
            tableBorders = Borders.NONE
            header {
                style(bold = true)
                row("ID", "Name", "Description", "Favr?", "Importance", "Deadline")
                align = TextAlign.CENTER
            }
            body {
                cellBorders = Borders.LEFT_RIGHT
                items.forEach {
                    row {
                        cell(it.id)
                        cell(it.name)
                        cell(it.description)
                        cell(if (it.favoured) "Yes" else "No") {
                            val color = if (it.favoured) TextColors.brightGreen else TextColors.brightRed
                            style(color = color, bold = true)
                        }
                        cell(it.importance) { style(color = getImportanceColor(it.importance)) }
                        cell(it.deadline ?: "N/A")
                    }
                }
            }
            column(0) { width = ColumnWidth.Fixed(4); align = TextAlign.CENTER }
            column(1) { width = ColumnWidth.Expand(0.3) }
            column(2) { width = ColumnWidth.Expand(0.7) }
            column(3) { width = ColumnWidth.Fixed(7); align = TextAlign.CENTER }
            column(4) { width = ColumnWidth.Fixed(12); align = TextAlign.CENTER }
            column(5) { width = ColumnWidth.Fixed(12); align = TextAlign.CENTER }
        })
    }

    override fun run() {
        dataFactory.transaction {
            val category = getCategoryById(byUUID, categoryId)

            if (category === null)
                throw IdNotFoundException(categoryId, typeOf<TodoCategory>())
            val items = when (sortedBy) {
                TodoItemFields.Id -> category.items.notForUpdate().orderBy(TodoItems.id to order)
                TodoItemFields.Name -> category.items.notForUpdate().orderBy(TodoItems.name to order)
                TodoItemFields.Description -> category.items.notForUpdate().orderBy(TodoItems.name to order)
                TodoItemFields.Favoured -> category.items.notForUpdate().orderBy(TodoItems.favoured to order)
                TodoItemFields.Importance -> category.items.notForUpdate().orderBy(TodoItems.importance to order)
                TodoItemFields.Deadline -> category.items.notForUpdate().orderBy(TodoItems.deadline to order)
            }

            if (outputJSON)
                outputJSON(items.map { it.toModel() })
            else
                outputTable(items)
        }
    }
}