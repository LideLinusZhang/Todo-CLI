import commands.ModifyItem
import data.TodoCategory
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.ItemImportance
import exceptions.IdNotFoundException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test

internal class ModifyItemTest : CommandTest() {

    @Test
    fun nonExistItem_ThrowIdNotFoundException() {
        // Arrange
        val command = ModifyItem(dataFactory, null)
        dataFactory.transaction {
            TodoCategory.new {
                name = "Physics"
                favoured = true
            }
        }

        //Act & Assert
        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1", "--field", "name", "a2")) }
    }

    @Test
    fun modifyItem_Successful() {
        // Arrange
        val command = ModifyItem(dataFactory, null)

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
                favoured = false
            }

            //Act & Assert
            assertDoesNotThrow { command.parse(arrayOf("1", "--field", "name", "a2")) }
        }
    }
}