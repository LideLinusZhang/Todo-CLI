import data.DataFactory
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
            val mathsItem = TodoCategory.findById(1)
            val physicsItem = TodoCategory.findById(2)

            assertEquals("Maths", mathsItem?.name)
            assertEquals(false, mathsItem?.favoured)
            assertEquals("Physics",physicsItem?.name)
            assertEquals(true, physicsItem?.favoured)
        }
    }
}