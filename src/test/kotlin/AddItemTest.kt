import com.github.ajalt.clikt.core.UsageError
import data.DataFactory
import data.TodoCategory
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.ItemImportance
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test

internal class AddItemTest {
    @Test
    fun addItem_Successful() {
        // Arrange
        val dataFactory = DataFactory()
        val command = AddItem(dataFactory)

        dataFactory.transaction {
            TodoCategory.new {
                name = "Physics"
                favoured = true
            }

        }

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("--search-category-by", "id", "1", "A1")) }
        dataFactory.clear()
    }

    @Test
    fun doubleItem_ThrowItemAlreadyExistException() {
        // Arrange
        val dataFactory = DataFactory()
        val command = AddItem(dataFactory)



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
        }


        //Act & Assert
        assertThrowsExactly(UsageError::class.java) { command.parse(arrayOf("--search-category-by", "id", "1", "A1")) }
        dataFactory.clear()
    }

}