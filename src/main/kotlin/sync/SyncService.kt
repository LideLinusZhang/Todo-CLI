package sync

import edu.uwaterloo.cs.todo.lib.TodoCategoryModel
import edu.uwaterloo.cs.todo.lib.TodoCategoryModificationModel
import edu.uwaterloo.cs.todo.lib.TodoItemModel
import edu.uwaterloo.cs.todo.lib.TodoItemModificationModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class SyncService(private val client: HttpClient, private val enabled: Boolean, url: String = "") {
    private val categoryOperationURL = URLBuilder(url).appendPathSegments("category").build()
    private val itemOperationURL = URLBuilder(url).appendPathSegments("item").build()

    protected fun finalize() {
        client.close()
    }

    suspend fun syncDatabase(): Pair<List<TodoCategoryModel>, List<TodoItemModel>> {
        if (!enabled) return Pair(listOf(), listOf())

        val categoriesOnServer = client.get(categoryOperationURL).body<List<TodoCategoryModel>>()
        val itemsOnServer = mutableListOf<TodoItemModel>()

        for (categoryModel: TodoCategoryModel in categoriesOnServer) {
            itemsOnServer.addAll(client.get(itemOperationURL) {
                parameter("categoryUniqueId", categoryModel.uniqueId)
            }.body<List<TodoItemModel>>())
        }

        return Pair(categoriesOnServer, itemsOnServer)
    }

    suspend fun addItem(categoryId: UUID, item: TodoItemModel) {
        if (!enabled) return

        client.post(itemOperationURL) {
            parameter("categoryUniqueId", categoryId)
            contentType(ContentType.Application.Json)
            setBody(item)
        }
    }

    suspend fun addCategory(category: TodoCategoryModel) {
        if (!enabled) return

        client.post(categoryOperationURL) {
            contentType(ContentType.Application.Json)
            setBody(category)
        }
    }

    suspend fun deleteItem(itemId: UUID) {
        if (!enabled) return

        client.delete(itemOperationURL) {
            parameter("id", itemId)
        }
    }

    suspend fun deleteCategory(categoryId: UUID) {
        if (!enabled) return

        client.delete(categoryOperationURL) {
            parameter("id", categoryId)
        }
    }

    suspend fun modifyItem(itemId: UUID, modification: TodoItemModificationModel) {
        if (!enabled) return

        client.post(itemOperationURL) {
            parameter("id", itemId)
            setBody(modification)
        }
    }

    suspend fun modifyCategory(categoryId: UUID, modification: TodoCategoryModificationModel) {
        if (!enabled) return

        client.post(categoryOperationURL) {
            parameter("id", categoryId)
            setBody(modification)
        }
    }
}