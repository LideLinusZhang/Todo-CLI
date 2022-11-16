import com.github.ajalt.clikt.core.PrintMessage
import commands.AddItem
import data.TodoCategory
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.ItemImportance
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test

internal class AddItemTest : CommandTest() {
    @Test
    fun addItem_Successful() {
        // Arrange
        val command = AddItem(dataFactory, null)

        dataFactory.transaction {
            TodoCategory.new {
                name = "Physics"
                favoured = true
            }
        }

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("--search-category-by", "id", "1", "A1")) }
    }

    @Test
    fun doubleItem_ThrowItemAlreadyExistException() {
        // Arrange
        val command = AddItem(dataFactory, null)

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
        }

        //Act & Assert
        assertThrowsExactly(PrintMessage::class.java) {
            command.parse(
                arrayOf(
                    "--search-category-by",
                    "id",
                    "1",
                    "A1"
                )
            )
        }
    }
}