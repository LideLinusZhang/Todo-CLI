import commands.DeleteCategory
import data.TodoCategory
import exceptions.IdNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DeleteCategoryTest : CommandTest() {
    @Test
    fun nonExistCategory_ThrowIdNotFoundException() {
        // Arrange
        val command = DeleteCategory(dataFactory, null)

        //Act & Assert
        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1")) }
    }

    @Test
    fun deleteSuccess_CategoryMatch() {
        // Arrange
        val command = DeleteCategory(dataFactory, null)

        dataFactory.transaction {
            TodoCategory.new {
                name = "Physics"
                favoured = true
            }
        }

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf(("1"))) }
        dataFactory.transaction { assertNull(TodoCategory.findById(1)) }
    }
}


