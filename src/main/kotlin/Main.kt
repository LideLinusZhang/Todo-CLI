import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import data.DataFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import sync.SyncServerConfig
import sync.SyncService
import java.io.File

class Cli : NoOpCliktCommand()

fun main(args: Array<String>) {
    val factory = DataFactory("jdbc:sqlite:./data.db")
    val configFile = File("config.json")
    val config: SyncServerConfig = if (configFile.exists()) {
        ConfigLoaderBuilder.default()
            .addFileSource(configFile)
            .build()
            .loadConfigOrThrow()
    } else SyncServerConfig("", false)
    val client = HttpClient(CIO) { install(ContentNegotiation) { json() } }
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