import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import data.TodoItem
import exceptions.IdNotFoundException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ListCategoryTest {




    @Test
    fun ListSuccess_ShowAllCategory() {
        // Arrange
        val dataFactory = DataFactory()
        val command = ListCategories(dataFactory)

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
            assertDoesNotThrow { command.parse(arrayOf("false"))}
        }


    }
}


