import commands.ListItems
import data.TodoCategory
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.ItemImportance
import exceptions.IdNotFoundException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test

internal class ListItemTest : CommandTest() {

    @Test
    fun nonExistAccordingList_ThrowIdNotFoundException() {
        // Arrange
        val command = ListItems(dataFactory)

        //Act & Assert
        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1")) }
        dataFactory.clearDatabase()
    }


    @Test
    fun listSuccess_ItemMatch() {
        // Arrange
        val command = ListItems(dataFactory)

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
            TodoItem.new {
                name = "midterm"
                importance = ItemImportance.NORMAL
                categoryId = category.uniqueId
                favoured = false
                description = String()
            }
            assertDoesNotThrow { command.parse(arrayOf(("1"))) }
        }
    }
}


