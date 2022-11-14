import commands.DeleteItem
import data.TodoCategory
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.ItemImportance
import exceptions.IdNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DeleteItemTest : CommandTest() {

    @Test
    fun nonExistItemNumber_ThrowIdNotFoundException() {
        // Arrange
        val command = DeleteItem(dataFactory, null)

        //Act & Assert
        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1")) }
    }

    @Test
    fun deleteSuccess_CategoryAndItemAllMatch() {
        // Arrange
        val command = DeleteItem(dataFactory, null)

        dataFactory.transaction {
            val category = TodoCategory.new {
                name = "Physics"
                favoured = true
            }
            TodoItem.new {
                name = "A1"
                importance = ItemImportance.NORMAL
                categoryId = category.uniqueId
                favoured = false
                description = String()
            }

            //Act & Assert
            assertDoesNotThrow { command.parse(arrayOf(("1"))) }
            assertNull(TodoItem.findById(1))
        }
    }
}


