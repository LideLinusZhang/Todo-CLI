import com.github.ajalt.clikt.completion.CompletionCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.UsageError
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
    val service: SyncService?
    val shouldSync: Boolean

    if (configFile.exists()) {
        val config: SyncServiceConfig = try {
            ConfigLoaderBuilder.default()
                .addFileSource(configFile)
                .build()
                .loadConfigOrThrow()
        } catch (_: Exception) {
            throw UsageError("Configuration corrupted.")
        }

        if (config.enabled) {
            val client = if (config.userCredential === null) {
                shouldSync = false
                HttpClient(CIO) { install(ContentNegotiation) { json() } }
            } else {
                shouldSync = true
                HttpClient(CIO) {
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
            }

            service = SyncService(client, config.serverUrl!!)
        } else {
            shouldSync = false
            service = null
        }
    } else {
        shouldSync = false
        service = null
    }

    val syncService = if(shouldSync) service else null
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
        SignUp(service),
        CompletionCommand()
    ).main(args)
}