import com.github.ajalt.clikt.core.PrintMessage
import commands.AddCategory
import data.TodoCategories
import data.TodoCategory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AddCategoryTest : CommandTest() {
    @Test
    fun addCategory_AllFieldsMatchInput() {
        // Arrange
        val command = AddCategory(dataFactory, null)

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("Maths")) }
        assertDoesNotThrow { command.parse(arrayOf("Physics", "--favoured")) }

        dataFactory.transaction {
            val mathsCategories = TodoCategory.find { TodoCategories.name eq "Maths" }
            val physicsCategories = TodoCategory.find { TodoCategories.name eq "Physics" }

            assertEquals(false, mathsCategories.empty())
            assertEquals(false, physicsCategories.empty())
            assertEquals(false, mathsCategories.first().favoured)
            assertEquals(true, physicsCategories.first().favoured)
        }
    }

    @Test
    fun doubleCategory_ThrowCategoryAlreadyExistException() {
        // Arrange
        val command = AddCategory(dataFactory, null)

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("Maths")) }
        assertThrowsExactly(PrintMessage::class.java) { command.parse(arrayOf("Maths", "--favoured")) }
    }
}