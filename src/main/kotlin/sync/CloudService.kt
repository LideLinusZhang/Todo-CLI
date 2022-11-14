package sync

import edu.uwaterloo.cs.todo.lib.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class CloudService(private val client: HttpClient, url: String) {

    private val categoryOperationURL = URLBuilder(url).appendPathSegments("category").build()
    private val itemOperationURL = URLBuilder(url).appendPathSegments("item").build()
    private val userOperationURL = URLBuilder(url).appendPathSegments("user").build()

    companion object {
        private fun createResponse(successful: Boolean, httpResponseBody: String): ServiceResult {
            return ServiceResult(successful, if (!successful) httpResponseBody else null)
        }
    }

    protected fun finalize() {
        client.close()
    }

    suspend fun syncDatabase(): Triple<ServiceResult, List<TodoCategoryModel>?, List<TodoItemModel>?> {
        val categoryResponse = client.get(categoryOperationURL)

        if (!categoryResponse.status.isSuccess())
            return Triple(ServiceResult(false, categoryResponse.body<String>()), null, null)

        val categoriesOnServer = categoryResponse.body<List<TodoCategoryModel>>()
        val itemsOnServer = mutableListOf<TodoItemModel>()

        for (categoryModel: TodoCategoryModel in categoriesOnServer) {
            val itemResponse = client.get(itemOperationURL) {
                parameter("categoryUniqueId", categoryModel.uniqueId)
            }

            if (!categoryResponse.status.isSuccess())
                return Triple(ServiceResult(false, itemResponse.body<String>()), null, null)

            itemsOnServer.addAll(itemResponse.body<List<TodoItemModel>>())
        }

        return Triple(ServiceResult(true, null), categoriesOnServer, itemsOnServer)
    }

    suspend fun addItem(categoryId: UUID, item: TodoItemModel): ServiceResult {
        val response = client.post(itemOperationURL) {
            parameter("categoryUniqueId", categoryId)
            contentType(ContentType.Application.Json)
            setBody(item)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun addCategory(category: TodoCategoryModel): ServiceResult {
        val response = client.post(categoryOperationURL) {
            contentType(ContentType.Application.Json)
            setBody(category)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun deleteItem(itemId: UUID): ServiceResult {
        val response = client.delete(itemOperationURL) {
            parameter("id", itemId)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun deleteCategory(categoryId: UUID): ServiceResult {
        val response = client.delete(categoryOperationURL) {
            parameter("id", categoryId)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun modifyItem(itemId: UUID, modification: TodoItemModificationModel): ServiceResult {
        val response = client.post(itemOperationURL) {
            parameter("id", itemId)
            setBody(modification)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun modifyCategory(categoryId: UUID, modification: TodoCategoryModificationModel): ServiceResult {
        val response = client.post(categoryOperationURL) {
            parameter("id", categoryId)
            setBody(modification)
        }

        return createResponse(response.status.isSuccess(), response.body())
    }

    suspend fun signUp(userName: String, hashedPassword: ByteArray): ServiceResult {
        val signUpURL = URLBuilder(userOperationURL).appendPathSegments("signup").build()

        val response = client.post(signUpURL) {
            contentType(ContentType.Application.Json)
            setBody(UserModel(userName, hashedPassword))
        }

        return createResponse(response.status.isSuccess(), response.body())
    }
}