import com.github.ajalt.clikt.completion.CompletionCommand
import com.github.ajalt.clikt.core.InvalidFileFormat
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import commands.*
import data.DataFactory
import sync.CloudService
import sync.CloudServiceConfig
import java.io.File

class Cli : NoOpCliktCommand()

fun main(args: Array<String>) {
    val factory = DataFactory(databaseConnectionString)
    val configFile = File(configFileName)
    var shouldSync = false

    val cloudService: CloudService? = if (configFile.exists()) {
        val config: CloudServiceConfig = try {
            ConfigLoaderBuilder.default()
                .addFileSource(configFile)
                .build()
                .loadConfigOrThrow()
        } catch (_: Exception) {
            throw InvalidFileFormat(configFileName, "Configuration corrupted.")
        }

        shouldSync = (config.userCredential === null)
        createCloudService(config)
    } else null

    val serviceForSyncing = if (shouldSync) cloudService else null

    Cli().subcommands(
        AddCategory(factory, serviceForSyncing),
        AddItem(factory, serviceForSyncing),
        DeleteCategory(factory, serviceForSyncing),
        DeleteItem(factory, serviceForSyncing),
        ModifyItem(factory, serviceForSyncing),
        ModifyCategory(factory, serviceForSyncing),
        ListCategories(factory),
        ListItems(factory),
        SyncFromServer(factory, serviceForSyncing),
        SignUp(cloudService),
        CompletionCommand()
    ).main(args)
}