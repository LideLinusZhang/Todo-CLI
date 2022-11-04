import data.DataFactory
import data.TodoCategory
import exceptions.IdNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DeleteCategoryTest {

    @Test
    fun nonExistCategory_ThrowIdNotFoundException() {
        // Arrange
        val dataFactory = DataFactory()
        val command = DeleteCategory(dataFactory)

        //Act & Assert

        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1")) }
        dataFactory.clear()
    }


    @Test
    fun deleteSuccess_CategoryMatch() {
        // Arrange
        val dataFactory = DataFactory()
        val command = DeleteCategory(dataFactory)

       dataFactory.transaction {
           TodoCategory.new {
               name = "Physics"
               favoured = true
           }
           assertDoesNotThrow { command.parse(arrayOf(("1")))}
           assertNull(TodoCategory.findById(1))
       }
        dataFactory.clear()
    }
}


