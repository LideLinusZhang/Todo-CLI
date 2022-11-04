import com.github.ajalt.clikt.core.UsageError
import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import data.TodoItem
import exceptions.IdNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ModifyCategoryTest {
    @Test
    fun nonExistCategory_ThrowIdNotFoundException() {
        // Arrange
        val dataFactory = DataFactory()
        val command = ModifyCategory(dataFactory)

        //Act & Assert

        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("Maths")) }
    }

    @Test
        fun ModifyCategory_Successful() {
        // Arrange
        val dataFactory = DataFactory()
        val command = ModifyCategory(dataFactory)


        dataFactory.transaction {
            val category = TodoCategory.new {
                name = "Physics"
                favoured = true
            }
        }


        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("1" , "name" , "Math"))}
    }
}