package sync

import data.*
import edu.uwaterloo.cs.todo.lib.TodoCategoryModel
import edu.uwaterloo.cs.todo.lib.TodoCategoryModificationModel
import edu.uwaterloo.cs.todo.lib.TodoItemModel
import edu.uwaterloo.cs.todo.lib.TodoItemModificationModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import java.util.*

class SyncService(url: String, private val enabled: Boolean, private val client: HttpClient) {
    private val categoryOperationURL = URLBuilder(url).appendPathSegments("category").build()
    private val itemOperationURL = URLBuilder(url).appendPathSegments("item").build()

    init {
        client.config { install(ContentNegotiation) { json() } }
    }

    protected fun finalize() {
        client.close()
    }

    suspend fun syncDatabase(dataFactory: DataFactory) {
        if (!enabled) return

        val listCategoriesResponse = client.request(categoryOperationURL) { method = HttpMethod.Get }

        for (categoryModel: TodoCategoryModel in listCategoriesResponse.body<List<TodoCategoryModel>>()) {
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

            val listItemsResponse = client.request(itemOperationURL) {
                method = HttpMethod.Get
                parameter("categoryUniqueId", categoryModel.uniqueId)
            }

            for (itemModel: TodoItemModel in listItemsResponse.body<List<TodoItemModel>>()) {
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
    }

    suspend fun addItem(categoryId: UUID, item: TodoItemModel) {
        if (!enabled) return

        client.request(itemOperationURL) {
            method = HttpMethod.Post
            parameter("categoryUniqueId", categoryId)
            contentType(ContentType.Application.Json)
            setBody(item)
        }
    }

    suspend fun addCategory(category: TodoCategoryModel) {
        if (!enabled) return

        client.request(categoryOperationURL) {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(category)
        }
    }

    suspend fun deleteItem(itemId: UUID) {
        if (!enabled) return

        client.request(itemOperationURL) {
            method = HttpMethod.Delete
            parameter("id", itemId)
        }
    }

    suspend fun deleteCategory(categoryId: UUID) {
        if (!enabled) return

        client.request(categoryOperationURL) {
            method = HttpMethod.Delete
            parameter("id", categoryId)
        }
    }

    suspend fun modifyItem(itemId: UUID, modification: TodoItemModificationModel) {
        if (!enabled) return

        client.request(itemOperationURL) {
            method = HttpMethod.Post
            parameter("id", itemId)
            setBody(modification)
        }
    }

    suspend fun modifyCategory(categoryId: UUID, modification: TodoCategoryModificationModel) {
        if (!enabled) return

        client.request(categoryOperationURL) {
            method = HttpMethod.Post
            parameter("id", categoryId)
            setBody(modification)
        }
    }
}