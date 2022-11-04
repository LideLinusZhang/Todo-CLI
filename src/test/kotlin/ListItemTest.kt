import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import data.TodoItem
import exceptions.IdNotFoundException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ListItemTest {

    @Test
    fun nonExistAccordingList_ThrowIdNotFoundException() {
        // Arrange
        val dataFactory = DataFactory()
        val command = ListItems(dataFactory)

        //Act & Assert

        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1")) }
    }


    @Test
    fun ListSuccess_ItemMatch() {
        // Arrange
        val dataFactory = DataFactory()
        val command = ListItems(dataFactory)

        dataFactory.transaction {
            val category = TodoCategory.new {
                name = "Physics"
                favoured = true
            }
            TodoItem.new {
                name = "A1"
                categoryId = category.uniqueId
            }
            TodoItem.new {
                name = "midterm"
                categoryId = category.uniqueId
            }
            assertDoesNotThrow { command.parse(arrayOf(("1")))}
        }


    }
}


