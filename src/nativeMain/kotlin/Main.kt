import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

class Cli : NoOpCliktCommand()

fun main(args: Array<String>) = Cli().subcommands(AddCategory(), AddItem(), DeleteCategory(), DeleteItem()).main(args)