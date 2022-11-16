import com.github.ajalt.clikt.completion.CompletionCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.subcommands
import commands.*
import data.DataFactory
import edu.uwaterloo.cs.todo.lib.readConfigFile
import java.io.File

class Cli : NoOpCliktCommand(name = "todo-cli")

fun main(args: Array<String>) {
    val dbFile = File(databaseFileName)
    val factory = DataFactory(databaseConnectionString, doSetup = !dbFile.exists())
    val readResult = readConfigFile()

    if (!readResult.first)
        throw PrintMessage("Cannot read configuration file. The file is either locked or corrupted.", error = true)

    val config = readResult.second
    var shouldSync = false

    val cloudService = if (config !== null) {
        shouldSync = (config.userCredential !== null)
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