package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.mordant.terminal.Terminal
import data.*
import edu.uwaterloo.cs.todo.lib.TodoCategoryModel
import edu.uwaterloo.cs.todo.lib.TodoItemModel
import kotlinx.coroutines.runBlocking
import sync.CloudService

class SyncFromServer(private val dataFactory: DataFactory, private val cloudService: CloudService?) :
    CliktCommand("Synchronize all categories and items from the server to local.") {
    private val terminal = Terminal()

    override fun run() {
        val syncResponse = runBlocking { cloudService?.syncDatabase() }
        if (syncResponse === null)
            throw PrintMessage("Cannot sync with the synchronization service disabled.", error = true)

        val serviceResponse = syncResponse.first
        if (!serviceResponse.successful)
            throw PrintMessage("Synchronization failed: ${serviceResponse.errorMessage}", error = true)

        val categoriesOnServer = syncResponse.second!!
        val itemsOnServer = syncResponse.third!!

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
                    item.favoured = itemModel.favoured
                    item.importance = itemModel.importance
                    item.deadline = itemModel.deadline
                    item.modifiedTime = itemModel.modifiedTime
                } else if (item === null) {
                    TodoItem.new {
                        name = itemModel.name
                        uniqueId = itemModel.uniqueId
                        description = itemModel.description
                        favoured = itemModel.favoured
                        importance = itemModel.importance
                        deadline = itemModel.deadline
                        categoryId = itemModel.categoryId
                        modifiedTime = itemModel.modifiedTime
                    }
                }
            }
        }

        terminal.println("Synchronization successful.")
    }
}