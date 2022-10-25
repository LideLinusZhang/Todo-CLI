import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AddCategoryTest {
    @Test
    fun addCategory_AllFieldsMatchInput() {
        // Arrange
        val dataFactory = DataFactory()
        val command = AddCategory(dataFactory)

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("Maths")) }
        assertDoesNotThrow { command.parse(arrayOf("Physics", "--favoured")) }

        dataFactory.transaction {
            val mathsItems = TodoCategory.find { TodoCategories.name eq "Maths"}
            val physicsItems = TodoCategory.find { TodoCategories.name eq "Physics"}

            assertEquals(false, mathsItems.empty())
            assertEquals(false, physicsItems.empty())
            assertEquals(false, mathsItems.first().favoured)
            assertEquals(true, physicsItems.first().favoured)
        }
    }
}