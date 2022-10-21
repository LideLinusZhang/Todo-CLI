import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import data.DataFactory

val factory = DataFactory()

class Cli : NoOpCliktCommand()

fun main(args: Array<String>) {
    Cli().subcommands(
        AddCategory(),
        AddItem(),
        DeleteCategory(),
        DeleteItem(),
        ModifyItem(),
        ModifyCategory(),
        ListCategories(),
        ListItems()
    ).main(args)
}