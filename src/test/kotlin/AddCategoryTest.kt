import com.github.ajalt.clikt.core.UsageError
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
            val mathsCategories = TodoCategory.find { TodoCategories.name eq "Maths"}
            val physicsCategories = TodoCategory.find { TodoCategories.name eq "Physics"}

            assertEquals(false, mathsCategories.empty())
            assertEquals(false, physicsCategories.empty())
            assertEquals(false, mathsCategories.first().favoured)
            assertEquals(true, physicsCategories.first().favoured)
        }
    }

    @Test
        fun DoubleCategory_ThrowCategoryAlreadyExistException() {
        // Arrange
        val dataFactory = DataFactory()
        val command = AddCategory(dataFactory)
        assertDoesNotThrow { command.parse(arrayOf("Maths")) }


        //Act & Assert
        assertThrowsExactly(UsageError::class.java) { command.parse(arrayOf("Maths", "--favoured")) }
    }
}