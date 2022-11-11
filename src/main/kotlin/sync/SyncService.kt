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

    companion object {
        private fun createResponse(successful: Boolean, httpResponseBody: String): ServiceResponse {
            return ServiceResponse(successful, if (!successful) httpResponseBody else null)
        }
    }

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

    suspend fun addItem(categoryId: UUID, item: TodoItemModel): ServiceResponse {
        val response = client.post(itemOperationURL) {
            parameter("categoryUniqueId", categoryId)
            contentType(ContentType.Application.Json)
            setBody(item)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun addCategory(category: TodoCategoryModel): ServiceResponse {
        val response = client.post(categoryOperationURL) {
            contentType(ContentType.Application.Json)
            setBody(category)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun deleteItem(itemId: UUID): ServiceResponse {
        val response = client.delete(itemOperationURL) {
            parameter("id", itemId)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun deleteCategory(categoryId: UUID): ServiceResponse {
        val response = client.delete(categoryOperationURL) {
            parameter("id", categoryId)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun modifyItem(itemId: UUID, modification: TodoItemModificationModel): ServiceResponse {
        val response = client.post(itemOperationURL) {
            parameter("id", itemId)
            setBody(modification)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun modifyCategory(categoryId: UUID, modification: TodoCategoryModificationModel): ServiceResponse {
        val response = client.post(categoryOperationURL) {
            parameter("id", categoryId)
            setBody(modification)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun signUp(userName: String, hashedPassword: ByteArray): ServiceResponse {
        val signUpURL = URLBuilder(userOperationURL).appendPathSegments("signup").build()

        val response = client.post(signUpURL) {
            setBody(UserModel(userName, hashedPassword))
        }

        return createResponse(response.status.isSuccess(), response.body())
    }
}