import commands.ModifyCategory
import data.TodoCategory
import exceptions.IdNotFoundException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test

internal class ModifyCategoryTest : CommandTest() {
    @Test
    fun nonExistCategory_ThrowIdNotFoundException() {
        // Arrange
        val command = ModifyCategory(dataFactory, null)

        //Act & Assert
        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1", "--field", "name", "Math")) }
    }

    @Test
    fun modifyCategory_Successful() {
        // Arrange
        val command = ModifyCategory(dataFactory, null)

        dataFactory.transaction {
            TodoCategory.new {
                name = "Physics"
                favoured = true
            }
        }

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("1", "--field", "name", "Math")) }
    }
}