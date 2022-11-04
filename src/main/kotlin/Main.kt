import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import data.*
import edu.uwaterloo.cs.todo.lib.TodoCategoryModel
import edu.uwaterloo.cs.todo.lib.TodoItemModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import sync.SyncServerConfig
import sync.SyncService
import java.io.File

class Cli : NoOpCliktCommand()

private fun syncFromServer(dataFactory: DataFactory, syncService: SyncService) {
    val modelsOnServer: Pair<List<TodoCategoryModel>, List<TodoItemModel>> =
        runBlocking { syncService.syncDatabase() }
    val categoriesOnServer = modelsOnServer.first
    val itemsOnServer = modelsOnServer.second

    for (categoryModel: TodoCategoryModel in categoriesOnServer) {
        dataFactory.transaction {
            val category = TodoCategory.find { TodoCategories.uniqueId eq categoryModel.uniqueId }.firstOrNull()

            if (category !== null && category.modifiedTime < categoryModel.modifiedTime) {
                category.name = categoryModel.name
                category.favoured = categoryModel.favoured
                category.modifiedTime = categoryModel.modifiedTime
            } else if (category === null) {
                TodoCategory.new {
                    name = categoryModel.name
                    favoured = categoryModel.favoured
                    uniqueId = categoryModel.uniqueId
                    modifiedTime = categoryModel.modifiedTime
                }
            }
        }
    }

    for (itemModel: TodoItemModel in itemsOnServer) {
        dataFactory.transaction {
            val item = TodoItem.find { TodoItems.uniqueId eq itemModel.uniqueId }.firstOrNull()

            if (item !== null && item.modifiedTime < itemModel.modifiedTime) {
                item.name = itemModel.name
                item.description = itemModel.description
                item.importance = itemModel.importance
                item.deadline = itemModel.deadline
                item.modifiedTime = itemModel.modifiedTime
            } else if (item === null) {
                TodoItem.new {
                    name = itemModel.name
                    description = itemModel.description
                    importance = itemModel.importance
                    deadline = itemModel.deadline
                    categoryId = itemModel.categoryId
                    modifiedTime = itemModel.modifiedTime
                }
            }
        }
    }
}

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

    syncFromServer(factory, syncService)

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