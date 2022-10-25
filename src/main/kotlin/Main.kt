import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import data.DataFactory

class Cli : NoOpCliktCommand()

fun main(args: Array<String>) {
    val factory = DataFactory("jdbc:sqlite:./data.db")

    Cli().subcommands(
        AddCategory(factory),
        AddItem(factory),
        DeleteCategory(factory),
        DeleteItem(factory),
        ModifyItem(factory),
        ModifyCategory(factory),
        ListCategories(factory),
        ListItems(factory)
    ).main(args)
}