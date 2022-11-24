package sync

import edu.uwaterloo.cs.todo.lib.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class CloudService(private val client: HttpClient, url: String) {

    private val categoryOperationURL = URLBuilder(url).appendPathSegments("category")
    private val itemOperationURL = URLBuilder(url).appendPathSegments("item")
    private val userOperationURL = URLBuilder(url).appendPathSegments("user")

    private val deleteURLSegment = "delete"
    private val modifyURLSegment = "modify"
    private val addURLSegment = "add"

    companion object {
        private fun createResponse(status: HttpStatusCode, httpResponseBody: String): ServiceResult {
            val isSuccess = status.isSuccess()
            val errorMessage: String? =
                if (isSuccess) null else if (httpResponseBody.isEmpty()) status.description else httpResponseBody

            return ServiceResult(isSuccess, errorMessage)
        }
    }

    protected fun finalize() {
        client.close()
    }

    suspend fun syncDatabase(): Triple<ServiceResult, List<TodoCategoryModel>?, List<TodoItemModel>?> {
        val categoryResponse = client.get(categoryOperationURL.build())

        if (!categoryResponse.status.isSuccess())
            return Triple(createResponse(categoryResponse.status, categoryResponse.body()), null, null)

        val categoriesOnServer = categoryResponse.body<List<TodoCategoryModel>>()
        val itemsOnServer = mutableListOf<TodoItemModel>()

        for (categoryModel: TodoCategoryModel in categoriesOnServer) {
            val itemResponse = client.get(itemOperationURL.build()) {
                parameter("categoryUniqueId", categoryModel.uniqueId)
            }

            if (!categoryResponse.status.isSuccess())
                return Triple(createResponse(categoryResponse.status, categoryResponse.body()), null, null)

            itemsOnServer.addAll(itemResponse.body<List<TodoItemModel>>())
        }

        return Triple(ServiceResult(true, null), categoriesOnServer, itemsOnServer)
    }

    suspend fun addItem(item: TodoItemModel): ServiceResult {
        val response = client.post(itemOperationURL.appendPathSegments(addURLSegment).build()) {
            contentType(ContentType.Application.Json)
            setBody(item)
        }

        return createResponse(response.status, response.body())
    }

    suspend fun addCategory(category: TodoCategoryModel): ServiceResult {
        val response = client.post(categoryOperationURL.appendPathSegments(addURLSegment).build()) {
            contentType(ContentType.Application.Json)
            setBody(category)
        }

        return createResponse(response.status, response.body())
    }

    suspend fun deleteItem(itemId: UUID): ServiceResult {
        val response = client.delete(itemOperationURL.appendPathSegments(deleteURLSegment).build()) {
            parameter("id", itemId)
        }

        return createResponse(response.status, response.body())
    }

    suspend fun deleteCategory(categoryId: UUID): ServiceResult {
        val response = client.delete(categoryOperationURL.appendPathSegments(deleteURLSegment).build()) {
            parameter("id", categoryId)
        }

        return createResponse(response.status, response.body())
    }

    suspend fun modifyItem(itemId: UUID, modification: TodoItemModificationModel): ServiceResult {
        val response = client.post(itemOperationURL.appendPathSegments(modifyURLSegment).build()) {
            contentType(ContentType.Application.Json)
            parameter("id", itemId)
            setBody(modification)
        }

        return createResponse(response.status, response.body())
    }

    suspend fun modifyCategory(categoryId: UUID, modification: TodoCategoryModificationModel): ServiceResult {
        val response = client.post(categoryOperationURL.appendPathSegments(modifyURLSegment).build()) {
            contentType(ContentType.Application.Json)
            parameter("id", categoryId)
            setBody(modification)
        }

        return createResponse(response.status, response.body())
    }

    suspend fun signUp(userName: String, hashedPassword: ByteArray): ServiceResult {
        val signUpURL = URLBuilder(userOperationURL).appendPathSegments("signup").build()

        val response = client.post(signUpURL) {
            contentType(ContentType.Application.Json)
            setBody(UserModel(userName, hashedPassword))
        }

        return createResponse(response.status, response.body())
    }
}