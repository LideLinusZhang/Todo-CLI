import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import data.TodoItem
import exceptions.IdNotFoundException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class DeleteCategoryTest {

    @Test
    fun nonExistCategory_ThrowIdNotFoundException() {
        // Arrange
        val dataFactory = DataFactory()
        val command = DeleteCategory(dataFactory)

        //Act & Assert

        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("Maths")) }
    }


    @Test
    fun DeleteSuccess_CategoryMatch() {
        // Arrange
        val dataFactory = DataFactory()
        val command = DeleteCategory(dataFactory)

       dataFactory.transaction {
           val category = TodoCategory.new {
               name = "Physics"
               favoured = true
           }
           assertDoesNotThrow { command.parse(arrayOf(("Physics")))}
           assertNull(TodoCategory.findById(1))
       }


    }
}


