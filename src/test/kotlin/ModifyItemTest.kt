import com.github.ajalt.clikt.core.UsageError
import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import data.TodoItem
import exceptions.IdNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ModifyItemTest {

    @Test
    fun nonExistItem_ThrowIdNotFoundException() {
        // Arrange
        val dataFactory = DataFactory()
        val command = ModifyItem(dataFactory)
        dataFactory.transaction {
            val category = TodoCategory.new {
                name = "Physics"
                favoured = true
            }
        }
        //Act & Assert

        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1" , "name" , "a2")) }
    }

    @Test
        fun ModifyItem_Successful() {
        // Arrange
        val dataFactory = DataFactory()
        val command = ModifyItem(dataFactory)


        dataFactory.transaction {
            val category = TodoCategory.new {
                name = "Physics"
                favoured = true
            }
            TodoItem.new {
                name = "A1"
                categoryId = category.uniqueId
            }
            //Act & Assert
            assertDoesNotThrow { command.parse(arrayOf("1" , "name" , "a2"))}

        }




    }
}