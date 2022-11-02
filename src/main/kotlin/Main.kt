import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import com.sksamuel.hoplite.json.JsonPropertySource
import data.DataFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.runBlocking
import sync.SyncServerConfig
import sync.SyncService

class Cli : NoOpCliktCommand()

fun main(args: Array<String>) {
    val factory = DataFactory("jdbc:sqlite:./data.db")
    val config = ConfigLoaderBuilder.default()
        .addSource(JsonPropertySource(""" { "serverURL": "localhost:8080", "enabled": false } """))
        .addResourceSource("/config.json", optional = true)
        .build()
        .loadConfigOrThrow<SyncServerConfig>()
    val client = HttpClient(CIO)
    val syncService = SyncService(client, config.enabled, config.serverURL)

    runBlocking { syncService.syncDatabase(factory) }

    Cli().subcommands(
        AddCategory(factory, syncService),
        AddItem(factory, syncService),
        DeleteCategory(factory, syncService),
        DeleteItem(factory, syncService),
        ModifyItem(factory, syncService),
        ModifyCategory(factory, syncService),
        ListCategories(factory),
        ListItems(factory)
    ).main(args)
}