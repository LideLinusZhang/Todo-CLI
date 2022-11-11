import data.*
import edu.uwaterloo.cs.todo.lib.TodoCategoryModel
import edu.uwaterloo.cs.todo.lib.TodoItemModel
import kotlinx.coroutines.runBlocking
import sync.SyncService
import java.util.*

fun syncFromServer(dataFactory: DataFactory, syncService: SyncService) {
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

fun getItemById(byUUID: Boolean, itemId: String): TodoItem? {
    return if (byUUID)
        TodoItem.find { TodoItems.uniqueId eq UUID.fromString(itemId) }.firstOrNull()
    else TodoItem.findById(itemId.toInt())
}

fun getCategoryById(byUUID: Boolean, categoryId: String): TodoCategory? {
    return if (byUUID)
        TodoCategory.find { TodoCategories.uniqueId eq UUID.fromString(categoryId) }.firstOrNull()
    else TodoCategory.findById(categoryId.toInt())
}