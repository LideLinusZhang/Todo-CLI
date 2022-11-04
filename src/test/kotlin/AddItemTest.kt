import com.github.ajalt.clikt.core.UsageError
import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import data.TodoItem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AddItemTest {
    @Test
    fun addItem_Successful() {
        // Arrange
        val dataFactory = DataFactory()
        val command = AddItem(dataFactory)

        dataFactory.transaction {
            val category = TodoCategory.new {
                name = "Physics"
                favoured = true
            }

        }

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("category.uniqueId" , "A1")) } 



    }

    @Test
        fun DoubleItem_ThrowItemAlreadyExistException() {
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
                categoryId = category.uniqueId
            }
        }


        //Act & Assert
        assertThrowsExactly(UsageError::class.java) { command.parse(arrayOf("category.uniqueId" , "A1")) }

    }

}