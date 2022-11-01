import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import data.DataFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import sync.SyncServerConfig
import sync.SyncService

class Cli : NoOpCliktCommand()

fun main(args: Array<String>) {
    val factory = DataFactory("jdbc:sqlite:./data.db")
    val config = ConfigLoaderBuilder.default()
        .addResourceSource("./config.json")
        .build()
        .loadConfigOrThrow<SyncServerConfig>()
    val client = HttpClient(CIO)
    val syncService = SyncService(config.serverURL, client)

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