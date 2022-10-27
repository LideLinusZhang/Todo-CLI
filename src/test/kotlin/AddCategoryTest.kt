import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import exceptions.IdNotFoundException
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
            val mathsCategories = TodoCategory.find { TodoCategories.name eq "Maths"}
            val physicsCategories = TodoCategory.find { TodoCategories.name eq "Physics"}

            assertEquals(false, mathsCategories.empty())
            assertEquals(false, physicsCategories.empty())
            assertEquals(false, mathsCategories.first().favoured)
            assertEquals(true, physicsCategories.first().favoured)
        }
    }

    @Test
    fun nonExistItemNumber_ThrowIdNotFoundException() {
        // Arrange
        val dataFactory = DataFactory()
        val command = DeleteItem(dataFactory)

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("35")) }



        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1")) }
    }
}
}