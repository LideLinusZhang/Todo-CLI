package sync

import edu.uwaterloo.cs.todo.lib.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class SyncService(private val client: HttpClient, url: String) {
    private val categoryOperationURL = URLBuilder(url).appendPathSegments("category").build()
    private val itemOperationURL = URLBuilder(url).appendPathSegments("item").build()
    private val userOperationURL = URLBuilder(url).appendPathSegments("user").build()

    protected fun finalize() {
        client.close()
    }

    suspend fun syncDatabase(): Pair<List<TodoCategoryModel>, List<TodoItemModel>> {
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
        client.post(itemOperationURL) {
            parameter("categoryUniqueId", categoryId)
            contentType(ContentType.Application.Json)
            setBody(item)
        }
    }

    suspend fun addCategory(category: TodoCategoryModel) {
        client.post(categoryOperationURL) {
            contentType(ContentType.Application.Json)
            setBody(category)
        }
    }

    suspend fun deleteItem(itemId: UUID) {
        client.delete(itemOperationURL) {
            parameter("id", itemId)
        }
    }

    suspend fun deleteCategory(categoryId: UUID) {
        client.delete(categoryOperationURL) {
            parameter("id", categoryId)
        }
    }

    suspend fun modifyItem(itemId: UUID, modification: TodoItemModificationModel) {
        client.post(itemOperationURL) {
            parameter("id", itemId)
            setBody(modification)
        }
    }

    suspend fun modifyCategory(categoryId: UUID, modification: TodoCategoryModificationModel) {
        client.post(categoryOperationURL) {
            parameter("id", categoryId)
            setBody(modification)
        }
    }

    suspend fun signUp(userName: String, hashedPassword: ByteArray) {
        val signUpURL = URLBuilder(userOperationURL).appendPathSegments("signup").build()

        client.post(signUpURL) {
            setBody(UserModel(userName, hashedPassword))
        }
    }
}