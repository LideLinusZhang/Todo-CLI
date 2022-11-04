import data.TodoCategory
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.ItemImportance
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

internal class ListCategoryTest: CommandTest() {
    @Test
    fun listSuccess_ShowAllCategory() {
        // Arrange
        val command = ListCategories(dataFactory)

        dataFactory.transaction {
            val category = TodoCategory.new {
                name = "Physics"
                favoured = true
            }
            TodoItem.new {
                name = "A1"
                importance = ItemImportance.NORMAL
                categoryId = category.uniqueId
                description = String()
            }
            TodoItem.new {
                name = "A2"
                importance = ItemImportance.NORMAL
                categoryId = category.uniqueId
                description = String()
            }
            assertDoesNotThrow { command.parse(arrayOf())}
        }
    }
}


