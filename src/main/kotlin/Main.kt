import com.github.ajalt.clikt.completion.CompletionCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import commands.*
import data.DataFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import sync.SyncService
import sync.SyncServiceConfig
import java.io.File

class Cli : NoOpCliktCommand()

fun main(args: Array<String>) {
    val factory = DataFactory("jdbc:sqlite:./data.db")
    val configFile = File("config.json")
    val syncService: SyncService?

    if (configFile.exists()) {
        val config: SyncServiceConfig = ConfigLoaderBuilder.default()
            .addFileSource(configFile)
            .build()
            .loadConfigOrThrow()

        if (config.enabled) {
            val client = if (config.userCredential === null)
                HttpClient(CIO) { install(ContentNegotiation) { json() } }
            else HttpClient(CIO) {
                install(Auth) {
                    digest {
                        credentials {
                            DigestAuthCredentials(
                                username = config.userCredential.userName,
                                password = config.userCredential.password
                            )
                        }
                        realm = edu.uwaterloo.cs.todo.lib.realm
                    }
                }
                install(ContentNegotiation) { json() }
            }

            syncService = SyncService(client, config.serverUrl!!)
        } else syncService = null
    } else syncService = null

    if (syncService !== null)
        syncFromServer(factory, syncService)

    Cli().subcommands(
        AddCategory(factory, syncService),
        AddItem(factory, syncService),
        DeleteCategory(factory, syncService),
        DeleteItem(factory, syncService),
        ModifyItem(factory, syncService),
        ModifyCategory(factory, syncService),
        ListCategories(factory),
        ListItems(factory),
        SignUp(syncService),
        CompletionCommand()
    ).main(args)
}