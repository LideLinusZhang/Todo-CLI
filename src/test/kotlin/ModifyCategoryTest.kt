import data.DataFactory
import data.TodoCategory
import exceptions.IdNotFoundException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test

internal class ModifyCategoryTest {
    @Test
    fun nonExistCategory_ThrowIdNotFoundException() {
        // Arrange
        val dataFactory = DataFactory()
        val command = ModifyCategory(dataFactory)

        //Act & Assert
        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1", "--field", "name", "Math")) }
        dataFactory.clear()
    }

    @Test
    fun modifyCategory_Successful() {
        // Arrange
        val dataFactory = DataFactory()
        val command = ModifyCategory(dataFactory)

        dataFactory.transaction {
            TodoCategory.new {
                name = "Physics"
                favoured = true
            }
        }

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("1", "--field", "name", "Math")) }
        dataFactory.clear()
    }
}