import data.TodoCategories
import data.TodoCategory
import data.TodoItem
import data.TodoItems
import java.util.*

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